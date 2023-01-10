package com.uraltrans.logisticparamservice.aop;

import com.uraltrans.logisticparamservice.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Slf4j
@Component
public class ScheduledTasksLogging {
    private static final Map<String, String> messages;

    static {
        messages = new HashMap<>();
        messages.put("loadCargos", "Сохранение грузов");
        messages.put("loadStationHandbook", "Сохранение справочника станций");
        messages.put("loadDataWithDelay", "Сохранение рейсов");
        messages.put("loadFlightProfits", "Сохранение доходности");
        messages.put("loadFlightAddressings", "Сохранение адресации");
        messages.put("loadGeocodes", "Получение координат станций");
        messages.put("loadNoDetailsWagons", "Сохранение вагонов без реквизитов");
        messages.put("updateCoordinates", "Обновление координат станций");
        messages.put("saveAllSecondEmptyFlights", "Сохранение вторых порожних рейсов");
        messages.put("loadSegmentsT14", "Сохранение анализа сегментации (t14)");
    }


    @Pointcut("execution(* com.uraltrans.logisticparamservice.service.schedule..*.*(..)) || " +
            "execution(* com.uraltrans.logisticparamservice.service.postgres.abstr.SecondEmptyFlightService.saveAllSecondEmptyFlights())")
    public void allScheduledPackageMethods(){}


    @Around("allScheduledPackageMethods()")
    public Object logAllScheduleServices(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodName = proceedingJoinPoint.getSignature().getName();
        logDataSave(true, "[По расписанию - старт] " + messages.get(methodName));
        Object methodResult;
        try{
            methodResult = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            logDataSave(false, "[По расписанию - ошибка] " + messages.get(methodName));
            throw e;
        }
        logDataSave(true, "[По расписанию - успех] " + messages.get(methodName));
        return methodResult;
    }


    private void logDataSave(boolean isSuccess, String message) {
        String actionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        message = message + FileUtils.DELIMITER + actionTime + FileUtils.DELIMITER + isSuccess;
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }
}
