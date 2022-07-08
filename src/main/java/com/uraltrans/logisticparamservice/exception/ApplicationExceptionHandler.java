package com.uraltrans.logisticparamservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Controller
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler
    protected String handleAllExceptions(Exception ex) {
        log.error("error: {}, {}", ex.getMessage(), ex.getStackTrace());
        ex.printStackTrace();
        return "redirect:/home?message=error";
    }
}
