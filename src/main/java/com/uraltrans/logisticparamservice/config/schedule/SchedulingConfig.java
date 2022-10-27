package com.uraltrans.logisticparamservice.config.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import com.uraltrans.logisticparamservice.service.schedule.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfig implements SchedulingConfigurer {
    private final LoadParameterService loadParameterService;

    private final ScheduleFlightsService scheduleFlightsService;
    private final ScheduleCargoService scheduleCargoService;
    private final ScheduleFlightProfitService scheduleFlightProfitService;
    private final ScheduleStationHandbookService scheduleStationHandbookService;
    private final ScheduleFlightAddressingService scheduleFlightAddressingService;
    private final ScheduleGeocodeService scheduleGeocodeService;
    private final ScheduleSecondEmptyFlightService scheduleSecondEmptyFlightService;

    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(16);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        List<Runnable> triggerTasks = Arrays.asList(
                scheduleFlightsService::saveDataWithDelay,
                scheduleCargoService::loadCargos,
                scheduleStationHandbookService::loadStationHandbook
        );

        for (Runnable task : triggerTasks) {
            registerTask(taskRegistrar, task, 0);
        }

        // registerTask(taskRegistrar, scheduleGeocodeService::loadGeocodes, 15);
        registerTask(taskRegistrar, scheduleStationHandbookService::updateCoordinates, 30);
        registerTask(taskRegistrar, scheduleFlightAddressingService::loadFlightAddressings, 160);
        registerTask(taskRegistrar, scheduleFlightProfitService::loadFlightProfits, 170);
        registerTask(taskRegistrar, scheduleSecondEmptyFlightService::loadSecondEmptyFlights, 180);

    }

    private void registerTask(ScheduledTaskRegistrar taskRegistrar, Runnable task, int additionalTime){
        taskRegistrar.addTriggerTask(
                task,
                triggerContext -> {
                    Calendar nextExecutionTime = new GregorianCalendar();
                    nextExecutionTime.setTime(getNextExecution(additionalTime));
                    return nextExecutionTime.getTime();
                }
        );
    }

    private void registerTaskAt(ScheduledTaskRegistrar taskRegistrar, Runnable task, LocalTime time){
        taskRegistrar.addTriggerTask(
                task,
                triggerContext -> {
                    LocalTime now = LocalTime.now();
                    if(now.isBefore(time)){
                        log.info("Время выгрузки: {}", LocalDateTime.of(LocalDate.now(), time));
                        return java.sql.Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), time));
                    }
                    log.info("Время выгрузки: {}", LocalDateTime.of(LocalDate.now(), time));
                    return java.sql.Timestamp.valueOf(LocalDateTime.of(LocalDate.now().plusDays(1), time));
                }
        );
    }

    private Date getNextExecution(int additionalTime) {
        LocalDateTime nextExecution = getExecutionTime().plusMinutes(additionalTime);
        log.info("Время выгрузки: {}", nextExecution);
        return java.sql.Timestamp.valueOf(nextExecution);
    }


    private LocalDateTime getExecutionTime(){
        LocalTime now = LocalTime.now();
        LocalTime nextExecutionTime = loadParameterService.getNextDataLoadTime();
        LocalDateTime nextExecution;
        if (now.isBefore(nextExecutionTime)) {
            nextExecution = LocalDateTime.of(LocalDate.now(), nextExecutionTime);
        } else {
            nextExecution = LocalDateTime.of(LocalDate.now().plusDays(1), nextExecutionTime);
        }
        return nextExecution;
    }
}
