package com.uraltrans.logisticparamservice.aop;

import com.uraltrans.logisticparamservice.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
@Slf4j
@Component
public class ScheduledTasksLogging {

    @AfterReturning("execution(* com.uraltrans.logisticparamservice.service.schedule.ScheduleStationHandbookService.loadStationHandbook())")
    public void successfulInvokeLoadStationHandbookMethod() {
        String message = getSaveDataLogMessage(true, "[По расписанию] Сохранение справочника станций");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterThrowing("execution(* com.uraltrans.logisticparamservice.service.schedule.ScheduleStationHandbookService.loadStationHandbook())")
    public void failureInvokeLoadStationHandbookMethod() {
        String message = getSaveDataLogMessage( false, "[По расписанию] Сохранение справочника станций");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterReturning("execution(* com.uraltrans.logisticparamservice.service.schedule.ScheduleFlightProfitService.loadFlightProfits())")
    public void successfulInvokeLoadFlightsProfitsMethod() {
        String message = getSaveDataLogMessage(true, "[По расписанию] Сохранение доходности рейсов");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterThrowing("execution(* com.uraltrans.logisticparamservice.service.schedule.ScheduleFlightProfitService.loadFlightProfits())")
    public void failureInvokeLoadFlightsProfitsMethod() {
        String message = getSaveDataLogMessage( false, "[По расписанию] Сохранение доходности рейсов");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterReturning("execution(* com.uraltrans.logisticparamservice.service.schedule.ScheduleCargoService.loadCargos())")
    public void successfulInvokeLoadCargosMethod() {
        String message = getSaveDataLogMessage(true, "[По расписанию] Сохранение грузов");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterThrowing("execution(* com.uraltrans.logisticparamservice.service.schedule.ScheduleCargoService.loadCargos())")
    public void failureInvokeLoadCargosMethod() {
        String message = getSaveDataLogMessage( false, "[По расписанию] Сохранение грузов");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterReturning("execution(* com.uraltrans.logisticparamservice.service.schedule.ScheduleActualFlightService.loadActualFlights())")
    public void successfulInvokeLoadActualFlightsMethod() {
        String message = getSaveDataLogMessage(true, "[По расписанию] Сохранение факта перевозок");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterThrowing("execution(* com.uraltrans.logisticparamservice.service.schedule.ScheduleActualFlightService.loadActualFlights())")
    public void failureInvokeLoadActualFlightsMethod() {
        String message = getSaveDataLogMessage( false, "[По расписанию] Сохранение факта перевозок");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterReturning("execution(* com.uraltrans.logisticparamservice.service.schedule.ScheduleFlightRequirementService.loadFlightRequirements())")
    public void successfulInvokeLoadFlightRequirementsMethod() {
        String message = getSaveDataLogMessage(true, "[По расписанию] Сохранение потребности в перевозках");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterThrowing("execution(* com.uraltrans.logisticparamservice.service.schedule.ScheduleFlightRequirementService.loadFlightRequirements())")
    public void failureInvokeInvokeLoadFlightRequirementsMethod() {
        String message = getSaveDataLogMessage( false, "[По расписанию] Сохранение потребности в перевозках");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    private String getSaveDataLogMessage(boolean isSuccess, String message) {
        String actionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return message + FileUtils.DELIMITER + actionTime + FileUtils.DELIMITER + isSuccess;
    }
}
