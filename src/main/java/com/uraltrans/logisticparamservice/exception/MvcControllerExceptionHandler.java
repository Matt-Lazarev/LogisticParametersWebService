package com.uraltrans.logisticparamservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Slf4j
@Order(0)
@Controller
@ControllerAdvice(basePackages = "com.uraltrans.logisticparamservice.controller.mvc")
public class MvcControllerExceptionHandler {

    @ExceptionHandler
    protected String handleAllExceptions(Exception ex, RedirectAttributes redirectAttrs) {
        log.error("error: {}\n {}", ex.getMessage(), ex);
        ex.printStackTrace();
        redirectAttrs.addFlashAttribute("message", "error");
        return "redirect:/home";
    }
}
