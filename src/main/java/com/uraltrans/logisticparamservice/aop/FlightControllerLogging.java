package com.uraltrans.logisticparamservice.aop;

import com.uraltrans.logisticparamservice.dto.LoadDataRequestDto;
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

    @AfterReturning("execution(public String saveLoadedData(*))")
    public void successfulInvokeSaveLoadedDataMethod(JoinPoint joinPoint){
        String message = getSaveDataLogMessage(joinPoint, true);
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterThrowing("execution(public String saveLoadedData(*))")
    public void failureInvokeSaveLoadedDataMethod(JoinPoint joinPoint){
        String message = getSaveDataLogMessage(joinPoint, false);
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterReturning("execution(* com.uraltrans.logisticparamservice.controller.api.FlightRestController.getAllFlights())")
    public void successfulGetAllFlightsMethod(){
        String message = getLoadDataLogMessage(true, "Получение сырых данных");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterThrowing("execution(* com.uraltrans.logisticparamservice.controller.api.FlightRestController.getAllFlights())")
    public void failureGetAllFlightsMethod(){
        String message = getLoadDataLogMessage(false, "Получение сырых данных");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterReturning("execution(* com.uraltrans.logisticparamservice.controller.api.CarLoadingController.getAllCarLoadUnloadIdles())")
    public void successfulGetAllCarLoadIdlesMethod(){
        String message = getLoadDataLogMessage(true, "Получение сгруппированных данных о выгрузках и погрузках");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    @AfterThrowing("execution(* com.uraltrans.logisticparamservice.controller.api.CarLoadingController.getAllCarLoadUnloadIdles())")
    public void failureGetAllCarLoadIdlesMethod(){
        String message = getLoadDataLogMessage(false, "Получение сгруппированных данных о выгрузках и погрузках");
        log.info("{}", message);
        FileUtils.writeActionLog(message);
    }

    private String getSaveDataLogMessage(JoinPoint joinPoint, boolean isSuccess){
        Object[] args = joinPoint.getArgs();
        LoadDataRequestDto arg = (LoadDataRequestDto) args[0];

        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(arg.getDaysToRetrieveData());
        String action = "Сохранение данных с " + from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                " до " + to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String actionTime =  LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return action + FileUtils.DELIMITER + actionTime  + FileUtils.DELIMITER + isSuccess;
    }

    private String getLoadDataLogMessage(boolean isSuccess, String message){
        String actionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return message + FileUtils.DELIMITER + actionTime  + FileUtils.DELIMITER + isSuccess;
    }
}
