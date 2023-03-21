package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.dislocation.DislocationRequest;
import com.uraltrans.logisticparamservice.dto.dislocation.DislocationResponse;
import com.uraltrans.logisticparamservice.dto.planfact.OrdersDto;
import com.uraltrans.logisticparamservice.entity.postgres.ActualFlight;
import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;
import com.uraltrans.logisticparamservice.exception.RepeatedRequestException;
import com.uraltrans.logisticparamservice.repository.integration.IntegrationDislocationRepository;
import com.uraltrans.logisticparamservice.repository.postgres.ActualFlightRepository;
import com.uraltrans.logisticparamservice.repository.postgres.PotentialFlightRepository;
import com.uraltrans.logisticparamservice.service.mapper.mapstruct.IntegrationDislocationMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ActualFlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightRequirementService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.StationHandbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.uraltrans.logisticparamservice.utils.MappingUtils.SHIFT_1C_YEARS;

@Service
@RequiredArgsConstructor
public class ActualFlightServiceImpl implements ActualFlightService {
    private static final Set<String> DISLOCATION_RESPONSES_CACHE = new HashSet<>();

    private static final Set<String> FILTER_VALUES = Set.of(
            "вывод", "аренда", "тр", "др", "ремонт", "отстой", "в тр", "промывка");
    private static final String FILTER_VALUE = "ремонт";

    private final FlightRequirementService flightRequirementService;
    private final StationHandbookService stationHandbookService;

    private final IntegrationDislocationRepository integrationDislocationRepository;
    private final ActualFlightRepository actualFlightRepository;
    private final PotentialFlightRepository potentialFlightRepository;
    private final IntegrationDislocationMapper integrationDislocationMapper;


    @Override
    public List<ActualFlight> getAllActualFlights() {
        return actualFlightRepository.findAll();
    }

    @Override
    public void saveAllActualFlights() {
        prepareNextActualFlightsSave();
        String dislocationDate = LocalDate.now().plusYears(SHIFT_1C_YEARS).toString();
        List<ActualFlight> actualFlights = integrationDislocationMapper.toActualFlightList(
                integrationDislocationRepository.getAllIntegrationDislocations(dislocationDate));
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
        List<PotentialFlight> potentialFlights = integrationDislocationMapper.toPotentialFlightList(
                integrationDislocationRepository.getAllIntegrationDislocations(dislocationDate));
        potentialFlights = filterPotentialFlights(potentialFlights);
        potentialFlightRepository.saveAllAndFlush(potentialFlights);
    }

    @Override
    public ActualFlight findByStationCodesAndVolume(String sourceStation, String destStation, BigDecimal volume) {
        return actualFlightRepository.findByStationCodesAndVolume(sourceStation, destStation, volume);
    }

    @Override
    public List<DislocationResponse> getAllByRequest(DislocationRequest request) {
        if(request == null){
            return integrationDislocationMapper.toDislocationResponseList(actualFlightRepository.findAll(), null);
        }

        if(DISLOCATION_RESPONSES_CACHE.contains(request.getId())){
            throw new RepeatedRequestException("Повторный запрос [id=" + request.getId() + "]");
        }

        if(!request.getWagonType().equalsIgnoreCase("Крытый")){
            return Collections.emptyList();
        }

        String region = stationHandbookService.getRegionByCode6(request.getDestinationStationCurrentFlight());

        List<DislocationResponse> responses = integrationDislocationMapper.toDislocationResponseList(
                actualFlightRepository.findAll()
                        .stream()
                        .filter(f -> {
                            String flightRegion = stationHandbookService.getRegionByCode6(f.getDestinationStationCode());
                            return region != null && region.equalsIgnoreCase(flightRegion);
                        })
                        .filter(f -> f.getVolume() != null && f.getVolume().compareTo(new BigDecimal(request.getVolume())) == 0)
                        .collect(Collectors.toList()),
                request.getId());

        DISLOCATION_RESPONSES_CACHE.add(request.getId());

        return responses;
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
                    OrdersDto requirement = flightRequirementService.getFlightRequirement(f);
                    if(requirement != null){
                        f.setRequirementOrders(requirement.getRequirementOrders());
                        f.setUtRate(requirement.getUtRate());
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
