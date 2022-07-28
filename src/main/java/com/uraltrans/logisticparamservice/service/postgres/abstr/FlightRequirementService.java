package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.FlightRequirement;

import java.util.List;

public interface FlightRequirementService {
    List<FlightRequirement> getAllFlightRequirements();
    void saveAllFlightRequirements();
}
