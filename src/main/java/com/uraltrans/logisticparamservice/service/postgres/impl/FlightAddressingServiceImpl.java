package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.FlightAddressing;
import com.uraltrans.logisticparamservice.entity.postgres.FlightRequirement;
import com.uraltrans.logisticparamservice.repository.postgres.FlightAddressingRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ActualFlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ClientOrderService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightAddressingService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightAddressingServiceImpl implements FlightAddressingService {
    private final FlightAddressingRepository flightAddressingRepository;
    private final FlightRequirementService flightRequirementService;
    private final ClientOrderService clientOrderService;
    private final ActualFlightService actualFlightService;
    @Override
    public List<FlightAddressing> getAll() {
        return flightAddressingRepository.findAll();
    }

    @Override
    public void saveAll() {
        List<FlightRequirement> allFlightRequirements = flightRequirementService.getAllFlightRequirements();
    }
}
