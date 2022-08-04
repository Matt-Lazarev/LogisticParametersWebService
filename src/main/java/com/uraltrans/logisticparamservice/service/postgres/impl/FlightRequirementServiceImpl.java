package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.FlightRequirement;
import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;
import com.uraltrans.logisticparamservice.repository.postgres.FlightRequirementRepository;
import com.uraltrans.logisticparamservice.service.mapper.FlightRequirementMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightRequirementServiceImpl implements FlightRequirementService {
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
    public FlightRequirement getFlightRequirement(PotentialFlight potentialFlight) {
        return flightRequirementRepository.findRequirementByVolumeAndStationCodes(
                potentialFlight.getVolume(), potentialFlight.getSourceStationCode(), potentialFlight.getDestinationStationCode()
        );
    }

    @Override
    public List<String> getAllSourceStationCodes() {
        return flightRequirementRepository.findAllSourceStationCodes();
    }

    private void filterFlightRequirements(List<FlightRequirement> flightRequirements) {
        flightRequirements
                .stream()
                .filter(f -> f.getRequirementOrders() < 0)
                .forEach(f -> f.setRequirementOrders(0));
    }

    private void prepareNextSave(){
        flightRequirementRepository.truncate();;
    }
}
