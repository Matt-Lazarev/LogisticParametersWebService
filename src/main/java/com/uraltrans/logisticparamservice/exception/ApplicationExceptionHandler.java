package com.uraltrans.logisticparamservice.exception;

import com.uraltrans.logisticparamservice.dto.station.StationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Controller
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(StationsNotFoundException.class)
    protected List<StationResponse> handleStationsNotFoundException(StationsNotFoundException ex){
        return Collections.singletonList(StationResponse
                .builder()
                .success("false")
                .errorText(ex.getMessage())
                .build());
    }

    @ExceptionHandler
    protected String handleAllExceptions(Exception ex, RedirectAttributes redirectAttrs) {
        log.error("error: {}, {}", ex.getMessage(), ex.getStackTrace());
        ex.printStackTrace();
        redirectAttrs.addFlashAttribute("message", "error");
        return "redirect:/home";
    }
}
