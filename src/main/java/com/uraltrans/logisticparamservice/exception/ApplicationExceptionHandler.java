package com.uraltrans.logisticparamservice.exception;

import com.uraltrans.logisticparamservice.dto.station.StationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Order(1)
@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(StationsNotFoundException.class)
    protected List<StationResponse> handleStationsNotFoundException(StationsNotFoundException ex) {
        log.error("error: {}\n {}", ex.getMessage(), ex);
        return Collections.singletonList(StationResponse
                .builder()
                .success("false")
                .errorText(ex.getMessage())
                .build());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpMethodNotSupportedException(Exception ex){
        log.error("error: {}\n {}", ex.getMessage(), ex);
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("message", ex.getMessage() + " [использован неверный HTTP метод]");
        errors.put("status", HttpStatus.BAD_REQUEST.name());
        errors.put("code", Integer.toString(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("error: {}\n {}", ex.getMessage(), ex);
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.put("status", HttpStatus.BAD_REQUEST.name());
            errors.put("message", errorMessage);
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public Map<String, String> handleAllExceptions(Exception ex) {
        log.error("error: {}\n {}", ex.getMessage(), ex);
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("message", ex.getMessage() + " [внутренняя ошибка сервера, проверьте корректность запроса]");
        errors.put("status", HttpStatus.INTERNAL_SERVER_ERROR.name());
        errors.put("code", Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        return errors;
    }
}
