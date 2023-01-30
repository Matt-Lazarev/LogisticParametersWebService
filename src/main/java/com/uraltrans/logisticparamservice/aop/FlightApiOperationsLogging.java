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
public class FlightApiOperationsLogging {
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
        String message = "Получение сырых данных";
        return handleReturn(proceedingJoinPoint, message);
    }


    @Around("execution(* com.uraltrans.logisticparamservice.controller.api.FlightIdleController.getAllCarLoadUnloadIdles())")
    public Object getAllCarLoadIdlesMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String message = "Получение сгруппированных данных о выгрузках и погрузках";
        return handleReturn(proceedingJoinPoint, message);
    }


    @Around("execution(* com.uraltrans.logisticparamservice.controller.api.FlightTimeDistanceController.getAllTimeDistances())")
    public Object getAllTimeDistancesMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String message = "Получение сгруппированных данных о дистанции и времени перевозки";
        return handleReturn(proceedingJoinPoint, message);
    }

    @Around("execution(* com.uraltrans.logisticparamservice.controller.api.FlightProfitController.*())")
    public Object logFlightProfitController(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String message = proceedingJoinPoint.getSignature().getName().startsWith("get")
                ? "Получение данных о доходности рейсов" : "Сохранение доходности рейсов";
        return handleReturn(proceedingJoinPoint, message);
    }

    @Around("execution(* com.uraltrans.logisticparamservice.controller.api.StationController.*())")
    public Object logStationController(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String message = proceedingJoinPoint.getSignature().getName().startsWith("get")
                ? "Получение данных о станциях" : "Сохранение справочника станций";
       return handleReturn(proceedingJoinPoint, message);
    }

    @Around("execution(* com.uraltrans.logisticparamservice.controller.api.CargoController.*())")
    public Object logCargoController(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String message = proceedingJoinPoint.getSignature().getName().startsWith("get")
                ? "Получение данных о грузах" : "Сохранение грузов";
        return handleReturn(proceedingJoinPoint, message);
    }

    @Around("execution(* com.uraltrans.logisticparamservice.controller.api.FlightAddressingController.*())")
    public Object logFlightAddressingController(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String message = proceedingJoinPoint.getSignature().getName().startsWith("getAllFlightAddressings")
                ? "Получение адресации вагонов" : "Сохранение адресации вагонов";
        return handleReturn(proceedingJoinPoint, message);
    }

    @Around("execution(* com.uraltrans.logisticparamservice.controller.api.SecondEmptyFlightController.*())")
    public Object logSecondEmptyFlightController(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String message = proceedingJoinPoint.getSignature().getName().startsWith("get")
                ? "Получение вторых порожних рейсов" : "Сохранение вторых порожних рейсов";
        return handleReturn(proceedingJoinPoint, message);
    }

    @Around("execution(* com.uraltrans.logisticparamservice.controller.api.NoDetailsWagonController.*())")
    public Object logNoDetailsWagonController(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String message = proceedingJoinPoint.getSignature().getName().startsWith("get")
                ? "Получение вагонов без реквизитов" : "Сохранение вагонов без реквизитов";
        return handleReturn(proceedingJoinPoint, message);
    }

    @Around("execution(* com.uraltrans.logisticparamservice.controller.api.SegmentationController.*())")
    public Object logSegmentationController(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String message = proceedingJoinPoint.getSignature().getName().startsWith("get")
                ? "Получение анализа сегментации (t14)" : "Сохранение анализа сегментации (t14)";
        return handleReturn(proceedingJoinPoint, message);
    }

    @Around("execution(* com.uraltrans.logisticparamservice.service.postgres.impl.RegionFlightServiceImpl.saveAllRegionFlights(..))")
    public Object logRegionSegmentationController(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String message = proceedingJoinPoint.getSignature().getName().startsWith("save")
                ? "Сохранение сегментации по регионам" : "";
        String logId = (String) proceedingJoinPoint.getArgs()[0];
        return handleSegmentationReturn(proceedingJoinPoint, message, logId);
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

    private Object handleReturn(ProceedingJoinPoint proceedingJoinPoint, String message) throws Throwable {
        Object methodResult;
        try {
            methodResult = proceedingJoinPoint.proceed();
        }
        catch (Throwable e) {
            logFlightDataGet(false, message);
            throw e;
        }
        logFlightDataGet(true, message);
        return methodResult;
    }

    private void logFlightDataGet(boolean isSuccess, String message) {
        String actionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        message = message + FileUtils.DELIMITER + actionTime + FileUtils.DELIMITER + isSuccess;
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    private Object handleSegmentationReturn(ProceedingJoinPoint proceedingJoinPoint, String message, String logId) throws Throwable {
        Object methodResult;
        try {
            methodResult = proceedingJoinPoint.proceed();
        }
        catch (Throwable e) {
            logSegmentationController(false, message, logId);
            throw e;
        }
        logSegmentationController(true, message, logId);
        return methodResult;
    }

    private void logSegmentationController(boolean isSuccess, String message, String logId) {
        String actionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        message = logId + FileUtils.DELIMITER +  message + FileUtils.DELIMITER + actionTime + FileUtils.DELIMITER + isSuccess;
        log.info("{}", message);
        FileUtils.writeRegionSegmentationActionLog(message);
    }
}
