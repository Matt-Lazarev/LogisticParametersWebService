package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.ratetariff.RateRequest;
import com.uraltrans.logisticparamservice.dto.ratetariff.RateTariffConfirmResponse;
import com.uraltrans.logisticparamservice.dto.ratetariff.TariffRequest;
import com.uraltrans.logisticparamservice.entity.postgres.ClientOrder;
import com.uraltrans.logisticparamservice.entity.postgres.FlightAddressing;
import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;
import com.uraltrans.logisticparamservice.repository.postgres.FlightAddressingRepository;
import com.uraltrans.logisticparamservice.service.mapper.FlightAddressingMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ActualFlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ClientOrderService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightAddressingService;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightAddressingServiceImpl implements FlightAddressingService {
    private static final String TARIFF_CALC_URL = "http://10.168.0.8/utc_srs/hs/calc/emptyflight";
    private static final String RATE_CALC_URL = "http://10.168.0.8/utc_srs/hs/calc/rateflight";

    private final FlightAddressingRepository flightAddressingRepository;
    private final FlightAddressingMapper flightAddressingMapper;
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

        flightAddressingRepository.saveAllAndFlush(addressings);
        sendTariffRequest(addressings);

        loadClientOrderCargosAndDates(addressings);
        flightAddressingRepository.saveAllAndFlush(addressings);
        sendRateRequest(addressings);
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

    private void prepareNextSave() {
        flightAddressingRepository.truncate();
        FileUtils.writeTariffRateErrors(Collections.emptyList(), false);
    }

    private List<FlightAddressing> getAllFlightAddressings(List<PotentialFlight> potentialFlights){
        return potentialFlights
                .stream()
                .flatMap(potentialFlight -> {
                    String currentStation = potentialFlight.getDestinationStationCode();
                    List<Map<String, Object>> rawData = flightAddressingRepository.findAllInRegion(currentStation);
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

    private void loadClientOrderCargosAndDates(List<FlightAddressing> addressings){
        addressings.forEach(addressing -> {
            BigDecimal volume = addressing.getVolume();
            String sourceStation = addressing.getSourceStationCode();
            String destStation = addressing.getDestinationStationCode();

            ClientOrder clientOrder = clientOrderService.findByStationCodesAndVolume(sourceStation, destStation, volume);
            if(clientOrder != null){
                addressing.setCargoCode(clientOrder.getCargoCode());
            }

            addressing.setDateFrom(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0))
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            addressing.setDateTo(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59))
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        });
    }
}
