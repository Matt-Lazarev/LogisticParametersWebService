package com.uraltrans.logisticparamservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StationsNotFoundException extends RuntimeException{
    public StationsNotFoundException(String message) {
        super(message);
    }
}
