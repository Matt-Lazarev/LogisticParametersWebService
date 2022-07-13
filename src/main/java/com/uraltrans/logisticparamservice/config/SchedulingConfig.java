package com.uraltrans.logisticparamservice.config;

import com.uraltrans.logisticparamservice.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfig implements SchedulingConfigurer {

    private final Environment env;
    private final ScheduleService scheduleService;

    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(10);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
                scheduleService::saveDataWithDelay,
                triggerContext -> {
                    Calendar nextExecutionTime = new GregorianCalendar();
                    Date nextExecution = scheduleService.getNextExecution();
                    nextExecutionTime.setTime(nextExecution);
                    return nextExecutionTime.getTime();
                }
        );
    }
}
