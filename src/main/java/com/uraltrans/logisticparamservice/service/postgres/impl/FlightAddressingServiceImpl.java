package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.addressing.AddressingRequest;
import com.uraltrans.logisticparamservice.dto.addressing.AddressingResponse;
import com.uraltrans.logisticparamservice.dto.cargo.CargoDto;
import com.uraltrans.logisticparamservice.dto.carinfo.CarRepairDto;
import com.uraltrans.logisticparamservice.dto.carinfo.CarThicknessDto;
import com.uraltrans.logisticparamservice.dto.freewagon.FreeWagonRequest;
import com.uraltrans.logisticparamservice.dto.freewagon.FreeWagonResponse;
import com.uraltrans.logisticparamservice.dto.ratetariff.RateRequest;
import com.uraltrans.logisticparamservice.dto.ratetariff.RateTariffConfirmResponse;
import com.uraltrans.logisticparamservice.dto.ratetariff.TariffRequest;
import com.uraltrans.logisticparamservice.entity.postgres.FlightAddressing;
import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;
import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;
import com.uraltrans.logisticparamservice.repository.integration.CarRepairInfoRepository;
import com.uraltrans.logisticparamservice.repository.postgres.FlightAddressingRepository;
import com.uraltrans.logisticparamservice.service.mapper.FlightAddressingMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.*;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import com.uraltrans.logisticparamservice.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightAddressingServiceImpl implements FlightAddressingService {
    private static final String TARIFF_CALC_URL = "http://10.168.0.8/utc_srs/hs/calc/emptyflight";
    private static final String RATE_CALC_URL = "http://10.168.0.8/utc_srs/hs/calc/rateflight";

    private final FlightAddressingRepository flightAddressingRepository;
    private final CarRepairInfoRepository carRepairInfoRepository;
    private final FlightAddressingMapper flightAddressingMapper;
    private final StationHandbookService stationHandbookService;
    private final CargoService cargoService;
    private final ClientOrderService clientOrderService;
    private final ActualFlightService actualFlightService;
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

        flightAddressingRepository.saveAllAndFlush(addressings);
        //sendTariffRequest(addressings);
        //sendRateRequest(addressings);
    }

    @Override
    public void updateTariff(String id, BigDecimal tariff) {
        Long entityId = Long.parseLong(id);
        if(flightAddressingRepository.existsById(entityId)){
            BigDecimal t = tariff != null ? tariff : BigDecimal.valueOf(0);
            flightAddressingRepository.updateTariffById(entityId, t);

            if(t.equals(BigDecimal.valueOf(0))){
                String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String error = "[" + time + "]" + flightAddressingRepository.findById(entityId).get() + " -> (тариф не расчитан)";
                FileUtils.writeTariffRateErrors(Collections.singletonList(error), true);
            }
        }
    }

    @Override
    public void updateRate(String id, BigDecimal rate) {
        Long entityId = Long.parseLong(id);
        if(flightAddressingRepository.existsById(entityId)) {
            BigDecimal r = rate != null ? rate : BigDecimal.valueOf(0);
            flightAddressingRepository.updateRateById(entityId, r);

            if (r.equals(BigDecimal.valueOf(0))) {
                String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String error = "[" + time + "]" + flightAddressingRepository.findById(entityId).get() + " -> (ставка не расчитана)";
                FileUtils.writeTariffRateErrors(Collections.singletonList(error), true);
            }
        }
    }

    @Override
    public List<AddressingResponse> getAllByAddressRequest(AddressingRequest request) {
        if(request == null){
            return flightAddressingMapper.mapToResponses(flightAddressingRepository.findAll());
        }

        if(!request.getWagonType().equalsIgnoreCase("Крытый")){
            return Collections.emptyList();
        }

        return flightAddressingMapper.mapToResponses(
                flightAddressingRepository.findAll()
                .stream()
                .filter(f -> request.getDepartureStation().equals(f.getSourceStationCode()))
                .filter(f -> f.getVolume().compareTo(new BigDecimal(request.getVolume())) == 0)
                .filter(f -> request.getCargoId().equals(f.getCargoCode()))
                .filter(f -> request.getDestinationStation() == null || request.getDestinationStation().equals(f.getDestinationStationCode()))
                .collect(Collectors.toList()));
    }

    @Override
    public List<FreeWagonResponse> getAllByFreeWagonRequest(FreeWagonRequest request) {
        if(request != null && !request.getWagonType().equalsIgnoreCase("Крытый")){
            return Collections.emptyList();
        }

        List<FlightAddressing> responses;
        if(request == null){
            responses = flightAddressingRepository.findAll();
        }
        else{
            responses = flightAddressingRepository.findAll()
                    .stream()
                    .filter(f -> request.getDepartureStation().equals(f.getSourceStationCode()))
                    .filter(f -> f.getVolume().compareTo(new BigDecimal(request.getVolume())) == 0)
                    .filter(f -> request.getCargoId().equals(f.getCargoCode()))
                    .filter(f -> request.getDestinationStation() == null || request.getDestinationStation().equals(f.getDestinationStationCode()))
                    .collect(Collectors.toList());
        }

        return flightAddressingMapper.mapToFreeWagonResponses(responses
                .stream()
                .filter(f -> !f.getRejected())
                .filter(f -> !f.getNonworkingPark())
                .filter(f -> f.getFeature2() == null || f.getFeature2().isEmpty())
                .filter(f -> f.getFeature12() == null || f.getFeature12().isEmpty())
                .filter(f -> f.getClientNextTask() == null || f.getClientNextTask().isEmpty())
                .collect(Collectors.toList()));
    }

    private void prepareNextSave() {
        flightAddressingRepository.truncate();
        FileUtils.writeTariffRateErrors(Collections.emptyList(), false);
    }

    private List<FlightAddressing> getAllFlightAddressings(List<PotentialFlight> potentialFlights){
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
                .filter(f -> f.getRequirementOrders() > 0)
                .collect(Collectors.toList());
    }

    private void sendTariffRequest(List<FlightAddressing> addressings) {
        List<TariffRequest> request = flightAddressingMapper.mapToTariffRequests(addressings);
        RateTariffConfirmResponse[] responses = restTemplate.postForObject(TARIFF_CALC_URL, request, RateTariffConfirmResponse[].class);
        handleRateTariffConfirmResponse(responses, true);
    }

    private void sendRateRequest(List<FlightAddressing> addressings) {
        List<RateRequest> request = flightAddressingMapper.mapToRateRequests(addressings);
        RateTariffConfirmResponse[] responses = restTemplate.postForObject(RATE_CALC_URL, request, RateTariffConfirmResponse[].class);
        handleRateTariffConfirmResponse(responses, false);
    }

    private void handleRateTariffConfirmResponse(RateTariffConfirmResponse[] responses, boolean isTariff){
        List<String> errors = new ArrayList<>();
        Arrays.stream(responses)
                .forEach(response -> {
                    Long id = Long.parseLong(response.getId());
                    Optional<FlightAddressing> entity = flightAddressingRepository.findById(id);
                    if(entity.isPresent() && response.getSuccess().equalsIgnoreCase("false")){
                        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        String message = isTariff ?  "тарифа" : "ставки";
                        errors.add("[" + time + "]" + entity.get() + " -> (запрос на расчет " + message + " не принят, причина: '" + response.getErrorText() +")'");
                    }
                });
        FileUtils.writeTariffRateErrors(errors, true);
    }

    private void loadCargosAndDates(List<FlightAddressing> addressings){
        List<FlightAddressing> additional = new ArrayList<>();
        addressings.forEach(addressing -> {
            BigDecimal volume = addressing.getVolume();
            String sourceStation = addressing.getSourceStationCode();

            List<CargoDto> clientOrders = clientOrderService.findBySourceStationCodeAndVolume(sourceStation, volume);
            if(clientOrders.size() != 0){
                addressing.setClientOrderCargoCode(clientOrders.get(0).getCargoCode());
                for(int i=1; i<clientOrders.size(); i++){
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

    private void loadUtRate(List<FlightAddressing> flightAddressings){
        flightAddressings
                .forEach(addressing -> {
                    if(addressing.getSourceStationCode().equals("072009")){
                        System.out.println("stop");
                    }
                    BigDecimal volume = addressing.getVolume();
                    String sourceStation = addressing.getSourceStationCode();
                    String destStation = addressing.getDestinationStationCode();

                    BigDecimal clientOrderUtRate = clientOrderService.findUtRateByStationCodesAndVolume(sourceStation, destStation, volume);
                    if(clientOrderUtRate == null){
                        clientOrderUtRate = clientOrderService.findUtRateBySourceStationCodeAndVolume(sourceStation, volume);
                    }
                    addressing.setUtRate(clientOrderUtRate);
                });
    }

    private void setDateAndDefaultTariff(List<FlightAddressing> flightAddressings){
        flightAddressings.forEach(addressing -> {
            addressing.setDateFrom(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0))
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            addressing.setDateTo(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59))
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            addressing.setTariff(BigDecimal.valueOf(0));
            addressing.setRate(BigDecimal.valueOf(0));
        });
    }

    private void setCargosName(List<FlightAddressing> addressings) {
        addressings.forEach(addressing -> {
            String cargoCode = addressing.getCargoCode();
            if(cargoCode != null){
                String cargoName = cargoService.findCargoNameByCode(cargoCode);
                addressing.setCargo(cargoName);
            }

            String clientCargoCode = addressing.getClientOrderCargoCode();
            if(clientCargoCode != null){
                String cargoName = cargoService.findCargoNameByCode(clientCargoCode);
                addressing.setClientOrderCargo(cargoName);
            }
        });
    }

    private void loadStationsParams(List<FlightAddressing> flightAddressings){
        flightAddressings.forEach(addressing -> {
            String sourceStation = addressing.getSourceStationCode();
            String destStation = addressing.getDestinationStationCode();
            String currFlightDestStation = addressing.getCurrentFlightDestStationCode();
            String dislocationStation = addressing.getDislocationStationCode();

            if(sourceStation != null){
                StationHandbook sh = stationHandbookService.findStationByCode6(sourceStation);
                if(sh != null){
                    addressing.setSourceStation(sh.getStation());
                    addressing.setSourceStationRegion(sh.getRegion());
                    addressing.setSourceStationRoad(sh.getRoad());
                }
            }

            if(destStation != null){
                StationHandbook sh = stationHandbookService.findStationByCode6(destStation);
                if(sh != null){
                    addressing.setDestinationStation(sh.getStation());
                    addressing.setDestinationStationRegion(sh.getRegion());
                    addressing.setDestinationStationRoad(sh.getRoad());
                }
            }

            if(currFlightDestStation != null){
                StationHandbook sh = stationHandbookService.findStationByCode6(currFlightDestStation);
                if(sh != null){
                    addressing.setCurrentFlightDestStation(sh.getStation());
                    addressing.setCurrentFlightDestStationRegion(sh.getRegion());
                    addressing.setCurrentFlightDestStationRoad(sh.getRoad());
                }
            }

            if(dislocationStation != null){
                StationHandbook sh = stationHandbookService.findStationByCode6(dislocationStation);
                if(sh != null){
                    addressing.setDislocationStation(sh.getStation());
                }
            }
        });
    }

    private void loadCarInfo(List<FlightAddressing> addressings) {
        Map<String, CarRepairDto> repairs = flightAddressingMapper.mapRawDataToCarRepairMap(
                carRepairInfoRepository.getAllCarRepairs(
                Mapper.to1cDate(LocalDate.now()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        Map<String, CarThicknessDto> thicknesses = flightAddressingMapper.mapRawDataToCarThicknessMap(
                carRepairInfoRepository.getAllCarWheelThicknesses(
                Mapper.to1cDate(LocalDate.now()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        addressings
                .forEach(addressing ->{
                    if(repairs.containsKey(addressing.getCarNumber())){
                        CarRepairDto repair = repairs.get(addressing.getCarNumber());
                        addressing.setNonworkingPark(repair.getNonworkingPark());
                        addressing.setRefurbished(repair.getRefurbished());
                        addressing.setRejected(repair.getRejected());
                    }

                    if(thicknesses.containsKey(addressing.getCarNumber())){
                        CarThicknessDto thickness = thicknesses.get(addressing.getCarNumber());
                        addressing.setThicknessWheel(thickness.getThicknessWheel());
                        addressing.setThicknessComb(thickness.getThicknessComb());
                    }

                    if(addressing.getNonworkingPark() == null){
                        addressing.setNonworkingPark(false);
                    }

                    if(addressing.getRejected() == null){
                        addressing.setRejected(false);
                    }

                    if(addressing.getRefurbished() == null){
                        addressing.setRefurbished(false);
                    }
                });
    }
}
