package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightIdleService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightTimeDistanceService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ScheduleFlightsService {
    private final FlightService flightService;
    private final FlightIdleService flightIdleService;
    private final FlightTimeDistanceService flightTimeDistanceService;
    private final LoadParameterService loadParameterService;

    public void saveDataWithDelay() {
        LoadParameters params = loadParameterService.getLoadParameters();
        flightService.saveAllFlights(params);
        flightIdleService.saveAll(params);
        flightTimeDistanceService.saveAll(params);
    }
}
