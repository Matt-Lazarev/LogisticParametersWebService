package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightIdleService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightTimeDistanceService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ScheduleFlightsService {
    private final FlightService flightService;
    private final FlightIdleService flightIdleService;
    private final FlightTimeDistanceService flightTimeDistanceService;
    private final LoadParameterService loadParameterService;

    @Transactional
    public void saveDataWithDelay() {
        LoadParameters params = loadParameterService.getLoadParameters();
        flightService.saveAllFlights(params);
        flightIdleService.saveAll(params);
        flightTimeDistanceService.saveAll(params);
        throw new RuntimeException();
    }

    @Transactional(readOnly = true)
    public Date getNextExecution() {
        LocalTime now = LocalTime.now();

        LocalTime nextExecutionTime = loadParameterService.getNextDataLoadTime();

        LocalDateTime nextExecution;
        if (now.isBefore(nextExecutionTime)) {
            nextExecution = LocalDateTime.of(LocalDate.now(), nextExecutionTime);
        } else {
            nextExecution = LocalDateTime.of(LocalDate.now().plusDays(1), nextExecutionTime);
        }
        return java.sql.Timestamp.valueOf(nextExecution);
    }
}
