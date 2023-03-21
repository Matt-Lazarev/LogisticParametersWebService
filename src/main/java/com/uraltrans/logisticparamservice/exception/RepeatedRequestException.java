package com.uraltrans.logisticparamservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RepeatedRequestException extends RuntimeException {
    public RepeatedRequestException(String message) {
        super(message);
    }
}
