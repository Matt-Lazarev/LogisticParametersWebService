package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.FlightRequirement;
import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;

import java.util.List;

public interface FlightRequirementService {
    List<FlightRequirement> getAllFlightRequirements();
    void saveAllFlightRequirements();

    Integer getFlightRequirement(PotentialFlight potentialFlight);

    List<String> getAllSourceStationCodes();
}
