package com.uraltrans.logisticparamservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Controller
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler
    protected String handleAllExceptions(Exception ex, RedirectAttributes redirectAttrs) {
        log.error("error: {}, {}", ex.getMessage(), ex.getStackTrace());
        ex.printStackTrace();
        redirectAttrs.addFlashAttribute("message", "error");
        return "redirect:/home";
    }
}
