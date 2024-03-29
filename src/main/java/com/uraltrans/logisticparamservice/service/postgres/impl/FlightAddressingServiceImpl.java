package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.addressing.AddressingRequest;
import com.uraltrans.logisticparamservice.dto.addressing.AddressingResponse;
import com.uraltrans.logisticparamservice.dto.cargo.CargoDto;
import com.uraltrans.logisticparamservice.dto.freewagon.FreeWagonRequest;
import com.uraltrans.logisticparamservice.dto.freewagon.FreeWagonResponse;
import com.uraltrans.logisticparamservice.dto.ratetariff.RateRequest;
import com.uraltrans.logisticparamservice.dto.ratetariff.RateTariffConfirmResponse;
import com.uraltrans.logisticparamservice.dto.ratetariff.TariffRequest;
import com.uraltrans.logisticparamservice.entity.integration.projection.IntegrationCarRepairProjection;
import com.uraltrans.logisticparamservice.entity.postgres.FlightAddressing;
import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;
import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;
import com.uraltrans.logisticparamservice.exception.RepeatedRequestException;
import com.uraltrans.logisticparamservice.repository.integration.IntegrationCarRepairRepository;
import com.uraltrans.logisticparamservice.repository.postgres.FlightAddressingRepository;
import com.uraltrans.logisticparamservice.service.mapper.FlightAddressingMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.*;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import com.uraltrans.logisticparamservice.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightAddressingServiceImpl implements FlightAddressingService {
    public static final String TARIFF_CALC_URL = "http://10.168.0.8/utc_srs/hs/calc/emptyflight";
    public static final String RATE_CALC_URL = "http://10.168.0.8/utc_srs/hs/calc/rateflight";

    private static final Set<String> ADDRESSING_RESPONSES_CACHE = new HashSet<>();
    private static final Set<String> FREEWAGON_RESPONSES_CACHE = new HashSet<>();

    @Value("${application.address}/calc/tariff")
    private String tariffCallbackUrl;

    @Value("${application.address}/calc/rate")
    private String rateCallbackUrl;

    private final FlightAddressingRepository flightAddressingRepository;
    private final IntegrationCarRepairRepository integrationCarRepairRepository;
    private final FlightAddressingMapper flightAddressingMapper;
    private final StationHandbookService stationHandbookService;
    private final CargoService cargoService;
    private final ClientOrderService clientOrderService;
    private final ActualFlightService actualFlightService;
    private final LoadParameterService loadParameterService;
    private final RestTemplate restTemplate;

    @Override
    public List<FlightAddressing> getAll() {
        return flightAddressingRepository.findAll();
    }

    @Override
    public void saveAll() {
        prepareNextSave();
        List<PotentialFlight> potentialFlights = actualFlightService.getAllPotentialFlights();
        List<FlightAddressing> addressings = getAllFlightAddressings(potentialFlights);

        loadCargosAndDates(addressings);
        loadStationsParams(addressings);
        loadUtRate(addressings);
        loadCarInfo(addressings);

        setTariffId(addressings);
        setRateId(addressings);

        flightAddressingRepository.saveAll(addressings);

        if (loadParameterService.getLoadParameters().getRateTariffState()) {
            String token = loadParameterService.getLoadParameters().getToken();

            Map<String, String> tariffHeaders = getHeaders("tariff", UUID.randomUUID().toString(), token);
            Map<String, String>  rateHeaders = getHeaders("rate", UUID.randomUUID().toString(), token);

            sendTariffRequest(groupForTariffRequest(addressings), tariffHeaders);
            sendRateRequest(groupForRateRequest(addressings), rateHeaders);
        }
    }

    @Override
    public void updateTariff(String id, BigDecimal tariff) {
        Integer entityId = Integer.parseInt(id);

        BigDecimal t = tariff != null ? tariff : BigDecimal.valueOf(0);
        flightAddressingRepository.updateTariffById(entityId, t);

        if (t.equals(BigDecimal.valueOf(0))) {
            List<FlightAddressing> flights = flightAddressingRepository.findAllByTariffId(Collections.singletonList(entityId));
            if(flights.size() > 0){
                String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String error = "[" + time + "]" + flights.get(0) + " -> (тариф не расчитан)";
                FileUtils.writeTariffRateErrors(Collections.singletonList(error), true);
            }
        }
    }

    @Override
    public void updateRate(String id, BigDecimal rate) {
        Integer entityId = Integer.parseInt(id);

        BigDecimal t = rate != null ? rate : BigDecimal.valueOf(0);
        flightAddressingRepository.updateRateById(entityId, t);

        if (t.equals(BigDecimal.valueOf(0))) {
            List<FlightAddressing> flights = flightAddressingRepository.findAllByRateId(Collections.singletonList(entityId));
            if(flights.size() > 0){
                String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String error = "[" + time + "]" + flights.get(0) + " -> (ставка не расчитана)";
                FileUtils.writeTariffRateErrors(Collections.singletonList(error), true);
            }
        }
    }

    @Override
    public List<AddressingResponse> getAllByAddressRequest(AddressingRequest request) {
        if (request == null) {
            return flightAddressingMapper.mapToResponses(flightAddressingRepository.findAll(), "null");
        }

        if (ADDRESSING_RESPONSES_CACHE.contains(request.getId())) {
            throw new RepeatedRequestException("Повторный запрос [id=" + request.getId() + "]");
        }

        if (!request.getWagonType().equalsIgnoreCase("Крытый")) {
            return Collections.emptyList();
        }

        List<AddressingResponse> responses = flightAddressingMapper.mapToResponses(
                filterFlightAddressingsByRequest(request.getDepartureStation(), request.getVolume(),
                        request.getCargoId(), request.getDestinationStation()),
                request.getId());

        ADDRESSING_RESPONSES_CACHE.add(request.getId());
        return responses;
    }

    @Override
    public List<FreeWagonResponse> getAllByFreeWagonRequest(FreeWagonRequest request) {
        if (request != null && FREEWAGON_RESPONSES_CACHE.contains(request.getId())) {
            throw new RepeatedRequestException("Повторный запрос [id=" + request.getId() + "]");
        }

        if (request != null && !request.getWagonType().equalsIgnoreCase("Крытый")) {
            return Collections.emptyList();
        }

        List<FlightAddressing> responses;
        if (request == null) {
            responses = flightAddressingRepository.findAll();
        } else {
            responses = filterFlightAddressingsByRequest(
                    request.getDepartureStation(), request.getVolume(),
                    request.getCargoId(), request.getDestinationStation());
        }

        List<FreeWagonResponse> freeWagonResponses = flightAddressingMapper.mapToFreeWagonResponses(responses
                        .stream()
                        .filter(f -> !f.getRejected())
                        .filter(f -> !f.getNonworkingPark())
                        .filter(f -> f.getFeature2() == null || f.getFeature2().isEmpty())
                        .filter(f -> f.getFeature12() == null || f.getFeature12().isEmpty())
                        .filter(f -> f.getClientNextTask() == null || f.getClientNextTask().isEmpty())
                        .collect(Collectors.toList()),
                request == null ? "null" : request.getId());

        if (request != null) {
            FREEWAGON_RESPONSES_CACHE.add(request.getId());
        }
        return freeWagonResponses;
    }

    private List<FlightAddressing> filterFlightAddressingsByRequest(
            String departureStation, String volume, String cargoId, String destinationStation) {
        return flightAddressingRepository.findAll()
                .stream()
                .filter(f -> departureStation.equals(f.getSourceStationCode()))
                .filter(f -> f.getVolume().compareTo(new BigDecimal(volume)) == 0)
                .filter(f -> cargoId.equals(f.getCargoCode()))
                .filter(f -> destinationStation == null || destinationStation.equals(f.getDestinationStationCode()))
                .collect(Collectors.toList());
    }

    private void prepareNextSave() {
        flightAddressingRepository.truncate();
        FileUtils.writeTariffRateErrors(Collections.emptyList(), false);
    }

    private List<FlightAddressing> getAllFlightAddressings(List<PotentialFlight> potentialFlights) {
        return potentialFlights
                .stream()
                .flatMap(potentialFlight -> {
                    String currentStation = potentialFlight.getDestinationStationCode();
                    BigDecimal volume = potentialFlight.getVolume();
                    List<Map<String, Object>> rawData = flightAddressingRepository.findAllInRegion(currentStation, volume);
                    return flightAddressingMapper
                            .mapRawDataToFlightAddressingList(rawData, potentialFlight)
                            .stream();
                })
                .filter(f -> f.getVolume() != null && !f.getVolume().equals(new BigDecimal(0)))
                .filter(f -> f.getRequirementOrders() > 0)
                .collect(Collectors.toList());
    }

    private void sendTariffRequest(List<FlightAddressing> addressings, Map<String, String> headers) {
        List<TariffRequest> request = flightAddressingMapper.mapToTariffRequests(addressings);
        Map<String, Object> namedRequest = new HashMap<>(Collections.singletonMap("details", request));
        namedRequest.putAll(headers);

        RateTariffConfirmResponse response = restTemplate.postForObject(TARIFF_CALC_URL, namedRequest, RateTariffConfirmResponse.class);
        handleRateTariffConfirmResponse(response, true);

        log.info("Отправлен запрос на расчет тарифа, UID: {}, SIZE: {}", headers.get("uid"), request.size());
    }

    private void sendRateRequest(List<FlightAddressing> addressings, Map<String, String> headers) {
        List<RateRequest> request = flightAddressingMapper.mapToRateRequests(addressings);
        Map<String, Object> namedRequest = new HashMap<>(Collections.singletonMap("details", request));
        namedRequest.putAll(headers);

        RateTariffConfirmResponse response = restTemplate.postForObject(RATE_CALC_URL, namedRequest, RateTariffConfirmResponse.class);
        handleRateTariffConfirmResponse(response, false);

        log.info("Отправлен запрос на расчет ставки, UID: {}, SIZE: {}", headers.get("uid"), request.size());
    }

    private Map<String, String>  getHeaders(String method, String uid, String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("uid", uid);
        headers.put("token", token);
        if (method.equalsIgnoreCase("tariff")) {
            headers.put("url", tariffCallbackUrl);
        } else {
            headers.put("url", rateCallbackUrl);
        }
        return headers;
    }

    private void handleRateTariffConfirmResponse(RateTariffConfirmResponse response, boolean isTariff) {
        if(response == null){
            return;
        }
        List<String> errors = new ArrayList<>();
        response.getDetails()
                .forEach(detail -> {
                    Long id = Long.parseLong(detail.getId());
                    List<FlightAddressing> list = flightAddressingRepository.findAllById(Collections.singletonList(id));
                    if (list.size() > 0 && detail.getSuccess().equalsIgnoreCase("false")) {
                        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        String message = isTariff ? "тарифа" : "ставки";
                        errors.add("[" + time + "]" + list.get(0) + " -> (запрос на расчет " + message + " не принят, причина: '" + detail.getErrorText() + "')");
                    }
                });
        FileUtils.writeTariffRateErrors(errors, true);
    }

    private void loadCargosAndDates(List<FlightAddressing> addressings) {
        List<FlightAddressing> additional = new ArrayList<>();
        addressings.forEach(addressing -> {
            BigDecimal volume = addressing.getVolume();
            String sourceStation = addressing.getSourceStationCode();

            List<CargoDto> clientOrders = clientOrderService.findBySourceStationCodeAndVolume(sourceStation, volume);
            if (clientOrders.size() != 0) {
                addressing.setClientOrderCargoCode(clientOrders.get(0).getCargoCode());
                for (int i = 1; i < clientOrders.size(); i++) {
                    FlightAddressing fa = new FlightAddressing(addressing);
                    fa.setClientOrderCargoCode(clientOrders.get(i).getCargoCode());
                    additional.add(fa);
                }
            }
        });
        setDateAndDefaultTariff(addressings);
        setDateAndDefaultTariff(additional);
        addressings.addAll(additional);
        setCargosName(addressings);
    }

    private void loadUtRate(List<FlightAddressing> flightAddressings) {
        flightAddressings
                .forEach(addressing -> {
                    if (addressing.getSourceStationCode().equals("072009")) {
                        System.out.println("stop");
                    }
                    BigDecimal volume = addressing.getVolume();
                    String sourceStation = addressing.getSourceStationCode();
                    String destStation = addressing.getDestinationStationCode();

                    BigDecimal clientOrderUtRate = clientOrderService.findUtRateByStationCodesAndVolume(sourceStation, destStation, volume);
                    if (clientOrderUtRate == null) {
                        clientOrderUtRate = clientOrderService.findUtRateBySourceStationCodeAndVolume(sourceStation, volume);
                    }
                    addressing.setUtRate(clientOrderUtRate);
                });
    }

    private void setDateAndDefaultTariff(List<FlightAddressing> flightAddressings) {
        flightAddressings.forEach(addressing -> {
            addressing.setDateFrom(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0))
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            addressing.setDateTo(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59))
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            addressing.setTariff(BigDecimal.valueOf(0));
            addressing.setRate(BigDecimal.valueOf(0));
        });
    }

    private void setCargosName(List<FlightAddressing> addressings) {
        addressings.forEach(addressing -> {
            String cargoCode = addressing.getCargoCode();
            if (cargoCode != null) {
                String cargoName = cargoService.getCargoNameByCode(cargoCode);
                addressing.setCargo(cargoName);
            }

            String clientCargoCode = addressing.getClientOrderCargoCode();
            if (clientCargoCode != null) {
                String cargoName = cargoService.getCargoNameByCode(clientCargoCode);
                addressing.setClientOrderCargo(cargoName);
            }
        });
    }

    private void loadStationsParams(List<FlightAddressing> flightAddressings) {
        flightAddressings.forEach(addressing -> {
            String sourceStation = addressing.getSourceStationCode();
            String destStation = addressing.getDestinationStationCode();
            String currFlightDestStation = addressing.getCurrentFlightDestStationCode();
            String dislocationStation = addressing.getDislocationStationCode();

            if (sourceStation != null) {
                StationHandbook sh = stationHandbookService.findStationByCode6(sourceStation);
                if (sh != null) {
                    addressing.setSourceStation(sh.getStation());
                    addressing.setSourceStationRegion(sh.getRegion());
                    addressing.setSourceStationRoad(sh.getRoad());
                }
            }

            if (destStation != null) {
                StationHandbook sh = stationHandbookService.findStationByCode6(destStation);
                if (sh != null) {
                    addressing.setDestinationStation(sh.getStation());
                    addressing.setDestinationStationRegion(sh.getRegion());
                    addressing.setDestinationStationRoad(sh.getRoad());
                }
            }

            if (currFlightDestStation != null) {
                StationHandbook sh = stationHandbookService.findStationByCode6(currFlightDestStation);
                if (sh != null) {
                    addressing.setCurrentFlightDestStation(sh.getStation());
                    addressing.setCurrentFlightDestStationRegion(sh.getRegion());
                    addressing.setCurrentFlightDestStationRoad(sh.getRoad());
                }
            }

            if (dislocationStation != null) {
                StationHandbook sh = stationHandbookService.findStationByCode6(dislocationStation);
                if (sh != null) {
                    addressing.setDislocationStation(sh.getStation());
                }
            }
        });
    }

    private void loadCarInfo(List<FlightAddressing> addressings) {
        Map<String, IntegrationCarRepairProjection> repairs = integrationCarRepairRepository.getAllCarRepairs(
                        MappingUtils.to1cDate(LocalDate.now()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .stream()
                .collect(Collectors.toMap(IntegrationCarRepairProjection::getCarNumber, Function.identity()));

        Map<String, IntegrationCarRepairProjection> thicknesses = integrationCarRepairRepository.getAllCarWheelThicknesses(
                        MappingUtils.to1cDate(LocalDate.now()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .stream()
                .collect(Collectors.toMap(IntegrationCarRepairProjection::getCarNumber, Function.identity()));

        addressings
                .forEach(addressing -> {
                    if (repairs.containsKey(addressing.getCarNumber())) {
                        IntegrationCarRepairProjection repair = repairs.get(addressing.getCarNumber());
                        addressing.setNonworkingPark(repair.getNonworkingPark().equals("1"));
                        addressing.setRefurbished(repair.getRefurbished().equals("1"));
                        addressing.setRejected(repair.getRejected().equals("1"));
                    }

                    if (thicknesses.containsKey(addressing.getCarNumber())) {
                        IntegrationCarRepairProjection thickness = thicknesses.get(addressing.getCarNumber());
                        addressing.setThicknessWheel(thickness.getThicknessWheel());
                        addressing.setThicknessComb(thickness.getThicknessComb());
                    }

                    if (addressing.getNonworkingPark() == null) {
                        addressing.setNonworkingPark(false);
                    }

                    if (addressing.getRejected() == null) {
                        addressing.setRejected(false);
                    }

                    if (addressing.getRefurbished() == null) {
                        addressing.setRefurbished(false);
                    }
                });
    }

    private void setTariffId(List<FlightAddressing> addressings) {
        Map<String, List<FlightAddressing>> uniqueAddressings = addressings
                .stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCurrentFlightDestStationCode() + a.getSourceStationCode() + a.getCargoCode() + a.getVolume(),
                        Collectors.toList()
                ));

        AtomicInteger tariffCount = new AtomicInteger(1);
        uniqueAddressings.forEach((k, v) -> {
            for (FlightAddressing addressing : v) {
                addressing.setTariffId(tariffCount.get());
            }
            tariffCount.incrementAndGet();
        });
    }

    private void setRateId(List<FlightAddressing> addressings) {
        Map<String, List<FlightAddressing>> uniqueAddressings = addressings
                .stream()
                .collect(Collectors.groupingBy(
                        a -> a.getSourceStationCode() + a.getDestinationStationCode() + a.getClientOrderCargoCode() + a.getVolume(),
                        Collectors.toList()
                ));

        AtomicInteger rateCount = new AtomicInteger(1);
        uniqueAddressings.forEach((k, v) -> {
            for (FlightAddressing addressing : v) {
                addressing.setRateId(rateCount.get());
            }
            rateCount.incrementAndGet();
        });
    }

    public List<FlightAddressing> groupForTariffRequest(List<FlightAddressing> addressings) {
        return new ArrayList<>(addressings
                .stream()
                .collect(Collectors.toMap(
                        a -> a.getCurrentFlightDestStationCode() + a.getSourceStationCode() + a.getCargoCode() + a.getVolume(),
                        a -> a,
                        (a1, a2) -> a1
                ))
                .values());
    }

    private List<FlightAddressing> groupForRateRequest(List<FlightAddressing> addressings) {
        return new ArrayList<>(addressings
                .stream()
                .collect(Collectors.toMap(
                        a -> a.getSourceStationCode() + a.getDestinationStationCode() + a.getClientOrderCargoCode() + a.getVolume(),
                        a -> a,
                        (a1, a2) -> a1
                ))
                .values());
    }
}
