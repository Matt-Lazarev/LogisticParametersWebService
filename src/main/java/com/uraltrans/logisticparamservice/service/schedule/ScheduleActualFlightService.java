package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.ActualFlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleActualFlightService {
    private final ActualFlightService actualFlightService;

    @Scheduled(cron = "0 0 4 * * *")
    public void loadActualFlights(){
        actualFlightService.saveAllActualFlights();
    }
}
