package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightProfitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleFlightProfitService {
    private final FlightProfitService flightProfitService;

    public void loadFlightProfits(){
        flightProfitService.saveAll();
    }
}
