package com.uraltrans.logisticparamservice.config.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import com.uraltrans.logisticparamservice.service.schedule.*;
import lombok.RequiredArgsConstructor;
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

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfig implements SchedulingConfigurer {
    private final LoadParameterService loadParameterService;

    private final ScheduleFlightsService scheduleFlightsService;
    private final ScheduleActualFlightService scheduleActualFlightService;
    private final ScheduleCargoService scheduleCargoService;
    private final ScheduleClientOrderService scheduleClientOrderService;
    private final ScheduleFlightRequirementService scheduleFlightRequirementService;
    private final ScheduleFlightProfitService scheduleFlightProfitService;
    private final ScheduleStationHandbookService stationHandbookService;

    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(16);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        List<Runnable> triggerTasks = Arrays.asList(
                scheduleFlightsService::saveDataWithDelay,
                scheduleActualFlightService::loadActualFlights,
                scheduleCargoService::loadCargos,
                scheduleClientOrderService::loadClientOrders,
                scheduleFlightProfitService::loadFlightProfits,
                stationHandbookService::loadStationHandbook
        );

        for(Runnable task : triggerTasks){
           registerTask(taskRegistrar, task, false);
        }

        registerTask(taskRegistrar, scheduleFlightRequirementService::loadFlightRequirements, true);
    }

    private void registerTask(ScheduledTaskRegistrar taskRegistrar, Runnable task, boolean isFlightRequirementTask){
        taskRegistrar.addTriggerTask(
                task,
                triggerContext -> {
                    Calendar nextExecutionTime = new GregorianCalendar();
                    nextExecutionTime.setTime(isFlightRequirementTask ? getFlightRequirementsNextExecution() : getNextExecution());
                    return nextExecutionTime.getTime();
                }
        );
    }

    private Date getNextExecution() {
        LocalDateTime nextExecution = getExecutionTime();
        System.err.println(nextExecution);
        return java.sql.Timestamp.valueOf(nextExecution);
    }

    private Date getFlightRequirementsNextExecution() {
        LocalDateTime nextExecution = getExecutionTime().plusSeconds(20);
        System.err.println(nextExecution);
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
