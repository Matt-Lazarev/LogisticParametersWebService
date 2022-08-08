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
    private final ScheduleCargoService scheduleCargoService;
    private final ScheduleFlightProfitService scheduleFlightProfitService;
    private final ScheduleStationHandbookService scheduleStationHandbookService;
    private final ScheduleFlightAddressingService scheduleFlightAddressingService;

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

        for(Runnable task : triggerTasks){
           registerTask(taskRegistrar, task, 0);
        }

        registerTask(taskRegistrar, scheduleFlightAddressingService::loadFlightAddressings, 4);
        registerTask(taskRegistrar, scheduleFlightProfitService::loadFlightProfits, 4);
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

    private Date getNextExecution(int additionalTime) {
        LocalDateTime nextExecution = getExecutionTime().plusMinutes(additionalTime);
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
