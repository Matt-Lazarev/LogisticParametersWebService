package com.uraltrans.logisticparamservice.exception;

import com.uraltrans.logisticparamservice.dto.station.StationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@ControllerAdvice
@Controller
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(StationsNotFoundException.class)
    protected List<StationResponse> handleStationsNotFoundException(StationsNotFoundException ex) {
        return Collections.singletonList(StationResponse
                .builder()
                .success("false")
                .errorText(ex.getMessage())
                .build());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.put("errorType", HttpStatus.BAD_REQUEST.name());
            errors.put("message", errorMessage);
        });
        return errors;
    }

    @ExceptionHandler
    protected String handleAllExceptions(Exception ex, RedirectAttributes redirectAttrs) {
        log.error("error: {}\n {}", ex.getMessage(), ex);
        ex.printStackTrace();
        redirectAttrs.addFlashAttribute("message", "error");
        return "redirect:/home";
    }
}
