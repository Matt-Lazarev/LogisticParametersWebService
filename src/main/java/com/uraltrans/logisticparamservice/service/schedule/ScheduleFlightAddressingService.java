package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.ActualFlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ClientOrderService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightAddressingService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleFlightAddressingService {
    private final ActualFlightService actualFlightService;
    private final ClientOrderService clientOrderService;
    private final FlightRequirementService flightRequirementService;
    private final FlightAddressingService flightAddressingService;

    public void loadFlightAddressings(){
        actualFlightService.saveAllActualFlights();
        clientOrderService.saveAllClientOrders();

        actualFlightService.saveAllPotentialFlights();
        flightRequirementService.saveAllFlightRequirements();

        flightAddressingService.saveAll();;
    }
}
