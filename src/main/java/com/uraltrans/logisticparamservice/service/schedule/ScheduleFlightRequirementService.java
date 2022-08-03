package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleFlightRequirementService {
    private final FlightRequirementService flightRequirementService;

    public void loadFlightRequirements(){
        flightRequirementService.saveAllFlightRequirements();
    }
}
