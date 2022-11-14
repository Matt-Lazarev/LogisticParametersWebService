package com.uraltrans.logisticparamservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Order(1)
@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler({StationsNotFoundException.class, RepeatedRequestException.class})
    protected ResponseEntity<?> handle(RuntimeException ex) {
        log.error("error: {}\n {}", ex.getMessage(), ex);
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("success", "false");
        errors.put("errorText", ex.getMessage());
        errors.put("code", Integer.toString(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpMethodNotSupportedException(Exception ex){
        log.error("error: {}\n {}", ex.getMessage(), ex);
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("success", "false");
        errors.put("errorText", ex.getMessage() + " [использован неверный HTTP метод]");
        errors.put("code", Integer.toString(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("error: {}\n {}", ex.getMessage(), ex);
        Map<String, Object> errors = new LinkedHashMap<>();
        List<String> fieldErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            fieldErrors.add(errorMessage);
        });
        errors.put("success", "false");
        errors.put("errorText", fieldErrors);
        return errors;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public Map<String, String> handleAllExceptions(Exception ex) {
        log.error("error: {}\n {}", ex.getMessage(), ex);
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("success", "false");
        errors.put("errorText", ex.getMessage() + " [внутренняя ошибка сервера, проверьте корректность запроса]");
        errors.put("code", Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        return errors;
    }
}
