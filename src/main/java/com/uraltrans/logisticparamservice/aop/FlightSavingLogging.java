package com.uraltrans.logisticparamservice.aop;

import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
@Slf4j
@Component
public class FlightSavingLogging {
    @Around("execution(* com.uraltrans.logisticparamservice.service.postgres.impl.FlightServiceImpl.saveAllFlights(..))")
    public Object saveLoadedDataMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object methodResult;
        try {
            methodResult = proceedingJoinPoint.proceed();
        }
        catch (Throwable e) {
            saveAndLogFlightLoad(proceedingJoinPoint, false);
            throw e;
        }
        saveAndLogFlightLoad(proceedingJoinPoint, true);
        return methodResult;
    }


    @Around("execution(* com.uraltrans.logisticparamservice.service.postgres.impl.FlightIdleServiceImpl.saveAll(..))")
    public Object saveFlightsIdleMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object methodResult;
        try {
            methodResult = proceedingJoinPoint.proceed();
        }
        catch (Throwable e) {
            saveAndLogFlightIdleLoad(proceedingJoinPoint, false);
            throw e;
        }
        saveAndLogFlightIdleLoad(proceedingJoinPoint, true);
        return methodResult;
    }


    @Around("execution(* com.uraltrans.logisticparamservice.service.postgres.impl.FlightTimeDistanceServiceImpl.saveAll(..))")
    public Object saveFlightsTimeDistanceMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object methodResult;
        try {
            methodResult = proceedingJoinPoint.proceed();
        }
        catch (Throwable e) {
            logFlightTimeDistanceLoad(proceedingJoinPoint, false);
            throw e;
        }
        logFlightTimeDistanceLoad(proceedingJoinPoint, true);
        return methodResult;
    }


    @Around("execution(* com.uraltrans.logisticparamservice.controller.api.FlightRestController.getAllFlights())")
    public Object getAllFlightsMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object methodResult;
        try {
            methodResult = proceedingJoinPoint.proceed();
        }
        catch (Throwable e) {
            logFlightDataGet(false, "Получение сырых данных");
            throw e;
        }
        logFlightDataGet(true, "Получение сырых данных");
        return methodResult;
    }


    @Around("execution(* com.uraltrans.logisticparamservice.controller.api.FlightIdleController.getAllCarLoadUnloadIdles())")
    public Object getAllCarLoadIdlesMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object methodResult;
        try {
            methodResult = proceedingJoinPoint.proceed();
        }
        catch (Throwable e) {
            logFlightDataGet(false, "Получение сгруппированных данных о выгрузках и погрузках");
            throw e;
        }
        logFlightDataGet(true, "Получение сгруппированных данных о выгрузках и погрузках");
        return methodResult;
    }


    @Around("execution(* com.uraltrans.logisticparamservice.controller.api.FlightTimeDistanceController.getAllTimeDistances())")
    public Object getAllTimeDistancesMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object methodResult;
        try {
            methodResult = proceedingJoinPoint.proceed();
        }
        catch (Throwable e) {
            logFlightDataGet(false, "Получение сгруппированных данных о дистанции и времени перевозки");
            throw e;
        }
        logFlightDataGet(true, "Получение сгруппированных данных о дистанции и времени перевозки");
        return methodResult;
    }


    private void saveAndLogFlightLoad(JoinPoint joinPoint, boolean isSuccess) {
        Object[] args = joinPoint.getArgs();
        LoadParameters arg = (LoadParameters) args[0];

        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(arg.getDaysToRetrieveData());
        String action = "Сохранение данных с " + from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                " до " + to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String actionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String message = action + FileUtils.DELIMITER + actionTime + FileUtils.DELIMITER + isSuccess;
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    private void saveAndLogFlightIdleLoad(JoinPoint joinPoint, boolean isSuccess) {
        Object[] args = joinPoint.getArgs();
        LoadParameters arg = (LoadParameters) args[0];
        String action = "Сохранение данных о простоях. Параметры: мин. вр. погрузки = " + arg.getMinLoadIdleDays() +
                ", макс. вр. погрузки " + arg.getMaxLoadIdleDays() + ", мин. вр. выгрузки = " + arg.getMinUnloadIdleDays() +
                ", макс. вр. выгрузки " + arg.getMaxUnloadIdleDays();
        String actionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String message = action + FileUtils.DELIMITER + actionTime + FileUtils.DELIMITER + isSuccess;
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    private void logFlightTimeDistanceLoad(JoinPoint joinPoint, boolean isSuccess) {
        Object[] args = joinPoint.getArgs();
        LoadParameters arg = (LoadParameters) args[0];
        String action = "Сохранение данных о дистанции и времени поездки. Параметры: мин. вр. перевозки = " +
                arg.getMinTravelTime() + ", макс. вр. перевозки = " + arg.getMaxTravelTime();
        String actionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String message = action + FileUtils.DELIMITER + actionTime + FileUtils.DELIMITER + isSuccess;
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    private void logFlightDataGet(boolean isSuccess, String message) {
        String actionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        message = message + FileUtils.DELIMITER + actionTime + FileUtils.DELIMITER + isSuccess;
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }
}
