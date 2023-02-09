package com.uraltrans.logisticparamservice.config.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionSegmentationParametersService;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfig implements SchedulingConfigurer {
    private final LoadParameterService loadParameterService;
    private final RegionSegmentationParametersService regionSegmentationParametersService;

    private final ScheduleFlightsService scheduleFlightsService;
    private final ScheduleCargoService scheduleCargoService;
    private final ScheduleFlightProfitService scheduleFlightProfitService;
    private final ScheduleStationHandbookService scheduleStationHandbookService;
    private final ScheduleFlightAddressingService scheduleFlightAddressingService;
    private final ScheduleGeocodeService scheduleGeocodeService;
    private final ScheduleNoDetailsWagonService scheduleNoDetailsWagonService;
    private final ScheduleSegmentationT14 scheduleSegmentationT14;
    private final ScheduleRegionSegmentationT15 scheduleRegionSegmentationT15;

    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(16);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        List<Runnable> triggerTasks = Arrays.asList(
                scheduleFlightsService::loadDataWithDelay,
                scheduleCargoService::loadCargos,
                scheduleStationHandbookService::loadStationHandbook
        );

        for (Runnable task : triggerTasks) {
            registerTask(taskRegistrar, task, 0);
        }

        Supplier<LocalTime> loadSegmentsT14TimeSupplier = ()-> LocalTime.parse(loadParameterService.getLoadParameters().getLoadTimeT14());
        registerTaskAt(taskRegistrar, scheduleSegmentationT14::loadSegmentsT14, loadSegmentsT14TimeSupplier);

        Supplier<LocalTime> loadRegionSegmentsT15TimeSupplier = ()-> LocalTime.parse(regionSegmentationParametersService.getParameters().getLoadTime());
        registerTaskAt(taskRegistrar, scheduleRegionSegmentationT15::loadRegionSegmentationT15, loadRegionSegmentsT15TimeSupplier);

        registerTask(taskRegistrar, scheduleGeocodeService::loadGeocodes, 20);
        registerTask(taskRegistrar, scheduleStationHandbookService::updateCoordinates, 25);
        registerTask(taskRegistrar, scheduleFlightProfitService::loadFlightProfits, 30);
        registerTask(taskRegistrar, scheduleFlightAddressingService::loadFlightAddressings, 60);

        registerTaskHourly(taskRegistrar, scheduleNoDetailsWagonService::loadNoDetailsWagons);
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

    private void registerTaskAt(ScheduledTaskRegistrar taskRegistrar, Runnable task, Supplier<LocalTime> timeSupplier){
        taskRegistrar.addTriggerTask(
                task,
                triggerContext -> {
                    LocalTime time = timeSupplier.get();
                    LocalDateTime nextLoadTime = LocalDateTime.of(LocalDate.now(), time);
                    LocalTime now = LocalTime.now();
                    if(now.isAfter(time)){
                        nextLoadTime = nextLoadTime.plusDays(1);
                    }
                    log.info("Время выгрузки: {}", nextLoadTime);
                    return java.sql.Timestamp.valueOf(nextLoadTime);
                }
        );
    }

    private void registerTaskHourly(ScheduledTaskRegistrar taskRegistrar, Runnable task) {
        taskRegistrar.addTriggerTask(
                task,
                triggerContext -> {
                    Calendar nextExecutionTime = new GregorianCalendar();
                    nextExecutionTime.setTime(getNextHourExecution());
                    return nextExecutionTime.getTime();
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

    private Date getNextHourExecution(){
        LocalDateTime nextExecution = LocalDateTime.now();
        if(nextExecution.getHour() == 23){
            nextExecution = LocalDateTime.of(nextExecution.toLocalDate().plusDays(1), LocalTime.of(0, 15, 0));
        }
        else {
            nextExecution = LocalDateTime.of(nextExecution.toLocalDate(), LocalTime.of(nextExecution.getHour()+1, 15, 0));
        }
        log.info("Время выгрузки: {}", nextExecution);
        return java.sql.Timestamp.valueOf(nextExecution);
    }
}
