package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.ActualFlight;
import com.uraltrans.logisticparamservice.entity.postgres.FlightRequirement;
import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;
import com.uraltrans.logisticparamservice.repository.integration.RawDislocationRepository;
import com.uraltrans.logisticparamservice.repository.postgres.ActualFlightRepository;
import com.uraltrans.logisticparamservice.repository.postgres.PotentialFlightRepository;
import com.uraltrans.logisticparamservice.service.mapper.ActualFlightMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ActualFlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightRequirementService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.StationHandbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActualFlightServiceImpl implements ActualFlightService {
    private static final int SHIFT_1C_YEARS = 2000;
    private static final Set<String> FILTER_VALUES = new HashSet<>(
            Arrays.asList("вывод", "аренда", "тр", "др", "ремонт", "отстой", "вывод", "в тр", "промывка"));
    private static final String FILTER_VALUE = "ремонт";

    private final FlightRequirementService flightRequirementService;

    private final RawDislocationRepository rawDislocationRepository;
    private final ActualFlightRepository actualFlightRepository;
    private final PotentialFlightRepository potentialFlightRepository;
    private final ActualFlightMapper actualFlightMapper;


    @Override
    public List<ActualFlight> getAllActualFlights() {
        return actualFlightRepository.findAll();
    }

    @Override
    public void saveAllActualFlights() {
        prepareNextActualFlightsSave();
        String dislocationDate = LocalDate.now().plusYears(SHIFT_1C_YEARS).toString();
        List<ActualFlight> actualFlights =
                actualFlightMapper.mapRawDataToActualFlightsList(rawDislocationRepository.getAllDislocations(dislocationDate));
        actualFlights = filterActualFlights(actualFlights);
        calculateCompletedAndInProgressOrders(actualFlights);
        actualFlightRepository.saveAll(actualFlights);
    }

    @Override
    public List<PotentialFlight> getAllPotentialFlights() {
        return potentialFlightRepository.findAll();
    }

    @Override
    public void saveAllPotentialFlights() {
        prepareNextPotentialFlightsSave();

        String dislocationDate = LocalDate.now().plusYears(SHIFT_1C_YEARS).toString();
        List<PotentialFlight> potentialFlights =
                actualFlightMapper.mapRawDataToPotentialFlightsList(rawDislocationRepository.getAllDislocations(dislocationDate));
        potentialFlights = filterPotentialFlights(potentialFlights);
        potentialFlightRepository.saveAllAndFlush(potentialFlights);
    }

    @Override
    public ActualFlight findByStationCodesAndVolume(String sourceStation, String destStation, BigDecimal volume) {
        return actualFlightRepository.findByStationCodesAndVolume(sourceStation, destStation, volume);
    }

    private void prepareNextActualFlightsSave(){
        actualFlightRepository.truncate();
    }

    private void prepareNextPotentialFlightsSave(){
        potentialFlightRepository.truncate();
    }

    private List<ActualFlight> filterActualFlights(List<ActualFlight> actualFlights){
        return actualFlights
                .stream()
                .filter(f -> !FILTER_VALUES.contains(f.getFeature2().toLowerCase()))
                .filter(f -> !FILTER_VALUES.contains(f.getFeature12().toLowerCase()))
                .filter(f -> !f.getCarState().toLowerCase().contains(FILTER_VALUE))
                .collect(Collectors.toList());
    }

    private List<PotentialFlight> filterPotentialFlights(List<PotentialFlight> potentialFlights){
        return potentialFlights
                .stream()
                .filter(f -> f.getLoaded() != null && f.getLoaded().equalsIgnoreCase("ГРУЖ"))
                .filter(f -> !FILTER_VALUES.contains(f.getFeature2().toLowerCase()))
                .filter(f -> !FILTER_VALUES.contains(f.getFeature12().toLowerCase()))
                .filter(f -> !f.getCarState().toLowerCase().contains(FILTER_VALUE))
                .filter(f -> f.getCarState().equalsIgnoreCase("гружёный ход"))
                .filter(f -> !f.getCarState().toLowerCase().contains("заказан"))
                .peek(f -> {
                    Integer requirement = flightRequirementService.getFlightRequirement(f);
                    if(requirement != null){
                        f.setRequirementOrders(requirement);
                    }
                })
                .filter(f -> f.getRequirementOrders() != null && f.getRequirementOrders() >= 1)
                .collect(Collectors.toList());
    }

    private void calculateCompletedAndInProgressOrders(List<ActualFlight> actualFlights){
        Set<String> filterValues = new HashSet<>(Arrays.asList("перестановка", "под погрузкой", "порожний ход"));
        String fleetFilterValue = "нсп";
        for(ActualFlight flight : actualFlights){
            if(filterValues.contains(flight.getCarState().toLowerCase())
                    && !fleetFilterValue.equalsIgnoreCase(flight.getFleetState())
                    && flight.getFeature2() != null && !flight.getFeature2().isEmpty()
                    && flight.getFeature12() != null && !flight.getFeature12().isEmpty()
            ){
                flight.setInProgressOrders(1);
                flight.setCompletedOrders(0);
            }
            else {
                flight.setInProgressOrders(0);
                flight.setCompletedOrders(1);
            }
        }
    }
}
