package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.FlightRequirement;
import com.uraltrans.logisticparamservice.repository.postgres.FlightRequirementRepository;
import com.uraltrans.logisticparamservice.service.mapper.FlightRequirementMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FlightRequirementServiceImpl implements FlightRequirementService {
    private final FlightRequirementRepository flightRequirementRepository;
    private final FlightRequirementMapper flightRequirementMapper;


    @Override
    @Transactional(readOnly = true)
    public List<FlightRequirement> getAllFlightRequirements() {
        return flightRequirementRepository.findAll();
    }

    @Override
    @Transactional
    public void saveAllFlightRequirements() {
        List<Map<String, Object>> maps = flightRequirementRepository.groupActualFlightsAndClientOrders();
        List<FlightRequirement> flightRequirements =
                flightRequirementMapper.mapToList(flightRequirementRepository.groupActualFlightsAndClientOrders());
        flightRequirementRepository.saveAll(flightRequirements);
    }

    private void prepareNextSave(){
        flightRequirementRepository.truncate();;
    }
}
