package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.ActualFlight;
import com.uraltrans.logisticparamservice.repository.integration.RawDislocationRepository;
import com.uraltrans.logisticparamservice.repository.postgres.ActualFlightRepository;
import com.uraltrans.logisticparamservice.service.mapper.ActualFlightMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ActualFlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActualFlightServiceImpl implements ActualFlightService {
    private static final int SHIFT_1C_YEARS = 2000;

    private final RawDislocationRepository rawDislocationRepository;
    private final ActualFlightRepository actualFlightRepository;
    private final ActualFlightMapper actualFlightMapper;


    @Override
    @Transactional(readOnly = true)
    public List<ActualFlight> getAllActualFlights() {
        return actualFlightRepository.findAll();
    }

    @Override
    @Transactional
    public void saveAllActualFlights() {
        prepareNextSave();
        String dislocationDate = LocalDate.now().plusYears(SHIFT_1C_YEARS).toString();
        List<ActualFlight> actualFlights =
                actualFlightMapper.mapRawDataToCargoList(rawDislocationRepository.getAllDislocations(dislocationDate));
        actualFlights = filterFlights(actualFlights);
        calculateCompletedAndInProgressOrders(actualFlights);
        actualFlightRepository.saveAll(actualFlights);
    }

    private void prepareNextSave(){
        actualFlightRepository.truncate();
    }

    private List<ActualFlight> filterFlights(List<ActualFlight> actualFlights){
        Set<String> filterValues = new HashSet<>(
                Arrays.asList("вывод", "аренда", "тр", "др", "ремонт", "отстой", "вывод", "в тр", "промывка"));
        String filterValue = "ремонт";
        return actualFlights
                .stream()
                .filter(f -> !filterValues.contains(f.getFeature2().toLowerCase()))
                .filter(f -> !filterValues.contains(f.getFeature12().toLowerCase()))
                .filter(f -> !filterValue.contains(f.getCarState().toLowerCase()))
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
