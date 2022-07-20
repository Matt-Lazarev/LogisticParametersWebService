package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightProfitService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleFlightProfitService {
    private final FlightProfitService flightProfitService;

    @Scheduled(cron = "${params.nextDataLoadTimeCron}")
    public void loadFlightProfits(){
        flightProfitService.saveAll();
    }
}
