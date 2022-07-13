package com.uraltrans.logisticparamservice.aop;

import com.uraltrans.logisticparamservice.dto.request.LoadDataRequestDto;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
@Slf4j
@Component
public class FlightControllerLogging {

    @AfterReturning("execution(* com.uraltrans.logisticparamservice.service.impl.FlightServiceImpl.saveAllFlights(..))")
    public void successfulInvokeSaveLoadedDataMethod(JoinPoint joinPoint) {
        String message = getSaveDataLogMessage(joinPoint, true);
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterThrowing("execution(* com.uraltrans.logisticparamservice.service.impl.FlightServiceImpl.saveAllFlights(..))")
    public void failureInvokeSaveLoadedDataMethod(JoinPoint joinPoint) {
        String message = getSaveDataLogMessage(joinPoint, false);
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterReturning("execution(* com.uraltrans.logisticparamservice.service.impl.FlightIdleServiceImpl.saveAll(..))")
    public void successfulInvokeSaveFlightsIdleMethod(JoinPoint joinPoint) {
        String message = getSaveIdleFlightsMessage(joinPoint, true);
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterThrowing("execution(* com.uraltrans.logisticparamservice.service.impl.FlightIdleServiceImpl.saveAll(..))")
    public void failureInvokeSaveFlightsIdleMethod(JoinPoint joinPoint) {
        String message = getSaveIdleFlightsMessage(joinPoint, false);
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterReturning("execution(* com.uraltrans.logisticparamservice.service.impl.FlightTimeDistanceServiceImpl.saveAll(..))")
    public void successfulInvokeSaveFlightsTimeDistanceMethod(JoinPoint joinPoint) {
        String message = getSaveTimeDistanceFlightsMessage(joinPoint, true);
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterThrowing("execution(* com.uraltrans.logisticparamservice.service.impl.FlightTimeDistanceServiceImpl.saveAll(..))")
    public void failureInvokeSaveFlightsTimeDistanceMethod(JoinPoint joinPoint) {
        String message = getSaveTimeDistanceFlightsMessage(joinPoint, false);
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterReturning("execution(* com.uraltrans.logisticparamservice.controller.api.FlightRestController.getAllFlights())")
    public void successfulGetAllFlightsMethod() {
        String message = getLoadDataLogMessage(true, "Получение сырых данных");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterThrowing("execution(* com.uraltrans.logisticparamservice.controller.api.FlightRestController.getAllFlights())")
    public void failureGetAllFlightsMethod() {
        String message = getLoadDataLogMessage(false, "Получение сырых данных");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterReturning("execution(* com.uraltrans.logisticparamservice.controller.api.FlightIdleController.getAllCarLoadUnloadIdles())")
    public void successfulGetAllCarLoadIdlesMethod() {
        String message = getLoadDataLogMessage(true, "Получение сгруппированных данных о выгрузках и погрузках");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterThrowing("execution(* com.uraltrans.logisticparamservice.controller.api.FlightIdleController.getAllCarLoadUnloadIdles())")
    public void failureGetAllCarLoadIdlesMethod() {
        String message = getLoadDataLogMessage(false, "Получение сгруппированных данных о выгрузках и погрузках");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterReturning("execution(* com.uraltrans.logisticparamservice.controller.api.FlightTimeDistanceController.getAllTimeDistances())")
    public void successfulGetAllTimeDistancesMethod() {
        String message = getLoadDataLogMessage(true, "Получение сгруппированных данных о дистанции времени перевозки");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterThrowing("execution(* com.uraltrans.logisticparamservice.controller.api.FlightTimeDistanceController.getAllTimeDistances())")
    public void failureGetAllTimeDistancesMethod() {
        String message = getLoadDataLogMessage(false, "Получение сгруппированных данных о дистанции времени перевозки");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }


    private String getSaveDataLogMessage(JoinPoint joinPoint, boolean isSuccess) {
        Object[] args = joinPoint.getArgs();
        LoadDataRequestDto arg = (LoadDataRequestDto) args[0];

        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(arg.getDaysToRetrieveData());
        String action = "Сохранение данных с " + from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                " до " + to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String actionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return action + FileUtils.DELIMITER + actionTime + FileUtils.DELIMITER + isSuccess;
    }

    private String getSaveIdleFlightsMessage(JoinPoint joinPoint, boolean isSuccess) {
        Object[] args = joinPoint.getArgs();
        LoadDataRequestDto arg = (LoadDataRequestDto) args[0];
        String action = "Сохранение данных о простоях. Параметры: мин. вр. погрузки = " + arg.getMinLoadIdleDays() +
                ", макс. вр. погрузки " + arg.getMaxLoadIdleDays() + ", мин. вр. выгрузки = " + arg.getMinUnloadIdleDays() +
                ", макс. вр. выгрузки " + arg.getMaxUnloadIdleDays();
        String actionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return action + FileUtils.DELIMITER + actionTime + FileUtils.DELIMITER + isSuccess;
    }

    private String getSaveTimeDistanceFlightsMessage(JoinPoint joinPoint, boolean isSuccess) {
        Object[] args = joinPoint.getArgs();
        LoadDataRequestDto arg = (LoadDataRequestDto) args[0];
        String action = "Сохранение данных о дистанции и времени поездки. Параметры: мин. вр. перевозки = " +
                arg.getMinTravelTime() + ", макс. вр. перевозки = " + arg.getMaxTravelTime();
        String actionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return action + FileUtils.DELIMITER + actionTime + FileUtils.DELIMITER + isSuccess;
    }

    private String getLoadDataLogMessage(boolean isSuccess, String message) {
        String actionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return message + FileUtils.DELIMITER + actionTime + FileUtils.DELIMITER + isSuccess;
    }
}
