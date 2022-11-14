package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.planfact.OrdersDto;
import com.uraltrans.logisticparamservice.dto.planfact.PlanFactRequest;
import com.uraltrans.logisticparamservice.dto.planfact.PlanFactResponse;
import com.uraltrans.logisticparamservice.entity.postgres.FlightRequirement;
import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;
import com.uraltrans.logisticparamservice.exception.RepeatedRequestException;
import com.uraltrans.logisticparamservice.repository.postgres.FlightRequirementRepository;
import com.uraltrans.logisticparamservice.service.mapper.FlightRequirementMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightRequirementServiceImpl implements FlightRequirementService {
    private static final Map<String, List<PlanFactResponse>> PLANFACT_RESPONSES_CACHE = new HashMap<>();

    private final FlightRequirementRepository flightRequirementRepository;
    private final FlightRequirementMapper flightRequirementMapper;


    @Override
    public List<FlightRequirement> getAllFlightRequirements() {
        return flightRequirementRepository.findAll();
    }

    @Override
    public void saveAllFlightRequirements() {
        prepareNextSave();
        List<FlightRequirement> flightRequirements =
                flightRequirementMapper.mapToList(flightRequirementRepository.groupActualFlightsAndClientOrders());
        filterFlightRequirements(flightRequirements);
        flightRequirementRepository.saveAll(flightRequirements);
    }

    @Override
    public OrdersDto getFlightRequirement(PotentialFlight potentialFlight) {
        OrdersDto fr = flightRequirementRepository.findRequirementByVolumeAndStationCodes(
                potentialFlight.getVolume(), potentialFlight.getSourceStationCode(), potentialFlight.getDestinationStationCode()
        );
        return fr != null
                ? fr
                : flightRequirementRepository.findAllRequirementByVolumeAndSourceStationCode(
                        potentialFlight.getVolume(), potentialFlight.getSourceStationCode());
    }

    @Override
    public List<String> getAllSourceStationCodes() {
        return flightRequirementRepository.findAllSourceStationCodes();
    }

    @Override
    public List<PlanFactResponse> getAllFlightRequirementsByRequest(PlanFactRequest request) {
        if(request == null){
            return flightRequirementMapper.mapToResponses(flightRequirementRepository.findAll(), "null");
        }

        if(PLANFACT_RESPONSES_CACHE.containsKey(request.getId())){
            throw new RepeatedRequestException("Повторный запрос [id=" + request.getId() + "]");
        }

        if(!request.getWagonType().equalsIgnoreCase("Крытый")){
            return Collections.emptyList();
        }

        List<PlanFactResponse> responses = flightRequirementMapper.mapToResponses(
                flightRequirementRepository.findAll()
                        .stream()
                        .filter(f -> request.getDepartureStation().equals(f.getSourceStationCode()))
                        .filter(f -> {
                            BigDecimal volume = new BigDecimal(request.getVolume());
                            return volume.compareTo(f.getVolumeFrom()) >= 0 && volume.compareTo(f.getVolumeTo()) <= 0;
                        })
                        .filter(f -> request.getDestinationStation() == null || request.getDestinationStation().equals(f.getDestinationStationCode()))
                        .collect(Collectors.toList()),
                request.getId());

        PLANFACT_RESPONSES_CACHE.put(request.getId(), responses);
        return responses;
    }

    private void filterFlightRequirements(List<FlightRequirement> flightRequirements) {
        flightRequirements
                .stream()
                .filter(f -> f.getRequirementOrders() < 0)
                .forEach(f -> f.setRequirementOrders(0));

        for (int i = 0; i < flightRequirements.size(); i++) {
            for (int j = i + 1; j < flightRequirements.size(); j++) {
                FlightRequirement fr1 = flightRequirements.get(i);
                FlightRequirement fr2 = flightRequirements.get(j);
                if (Objects.equals(fr1.getSourceStationCode(), fr2.getSourceStationCode())
                        && Objects.equals(fr1.getDestinationStationCode(), fr2.getDestinationStationCode())
                        && Objects.equals(fr1.getVolumeFrom(), fr2.getVolumeFrom())
                        && Objects.equals(fr1.getVolumeTo(), fr2.getVolumeTo()))
                {
                    fr1.setInPlanOrders(fr1.getInPlanOrders() + fr2.getInPlanOrders());
                    fr1.setRequirementOrders(fr1.getInPlanOrders() - fr1.getInProgressOrders() - fr1.getCompletedOrders());
                    flightRequirements.remove(j--);
                }
            }
        }
    }

    private void prepareNextSave() {
        flightRequirementRepository.truncate();
    }
}
