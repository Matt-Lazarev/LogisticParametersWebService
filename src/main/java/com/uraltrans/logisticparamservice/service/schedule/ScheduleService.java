package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.dto.request.LoadDataRequestDto;
import com.uraltrans.logisticparamservice.service.abstr.FlightIdleService;
import com.uraltrans.logisticparamservice.service.abstr.FlightService;
import com.uraltrans.logisticparamservice.service.abstr.FlightTimeDistanceService;
import com.uraltrans.logisticparamservice.utils.EnvUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final FlightService flightService;
    private final FlightIdleService flightIdleService;
    private final FlightTimeDistanceService flightTimeDistanceService;

    private final Environment env;

    public void saveDataWithDelay(){
        LoadDataRequestDto params = EnvUtils.getRequestParams(env);
        flightService.saveAllFlights(params);
        flightIdleService.saveAll(params);
        flightTimeDistanceService.saveAll(params);
    }

    public Date getNextExecution() {
        LocalTime now = LocalTime.now();

        LocalTime nextExecutionTime = EnvUtils.getNextDataLoadTime(env);

        LocalDateTime nextExecution;
        if(now.isBefore(nextExecutionTime)){
           nextExecution = LocalDateTime.of(LocalDate.now(), nextExecutionTime);
        }
        else {
            nextExecution = LocalDateTime.of(LocalDate.now().plusDays(1), nextExecutionTime);
        }

        System.err.println("next execution: " + nextExecution);
        return java.sql.Timestamp.valueOf(nextExecution);
    }
}
