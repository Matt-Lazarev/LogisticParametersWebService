package com.uraltrans.logisticparamservice.config.schedule;

import com.uraltrans.logisticparamservice.service.schedule.ScheduleFlightsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfig implements SchedulingConfigurer {

    private final ScheduleFlightsService scheduleFlightsService;

    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(15);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
                scheduleFlightsService::saveDataWithDelay,
                triggerContext -> {
                    Calendar nextExecutionTime = new GregorianCalendar();
                    Date nextExecution = scheduleFlightsService.getNextExecution();
                    nextExecutionTime.setTime(nextExecution);
                    return nextExecutionTime.getTime();
                }
        );
    }
}
