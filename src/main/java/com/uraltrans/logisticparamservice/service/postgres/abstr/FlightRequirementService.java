package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.dto.planfact.OrdersDto;
import com.uraltrans.logisticparamservice.dto.planfact.PlanFactRequest;
import com.uraltrans.logisticparamservice.dto.planfact.PlanFactResponse;
import com.uraltrans.logisticparamservice.entity.postgres.FlightRequirement;
import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;

import java.util.Arrays;
import java.util.List;

public interface FlightRequirementService {
    List<FlightRequirement> getAllFlightRequirements();
    void saveAllFlightRequirements();

    OrdersDto getFlightRequirement(PotentialFlight potentialFlight);

    List<String> getAllSourceStationCodes();

    List<PlanFactResponse> getAllFlightRequirementsByRequest(PlanFactRequest request);
}
