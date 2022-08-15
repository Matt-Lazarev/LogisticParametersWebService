package com.uraltrans.logisticparamservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PlanFactApiRequestException extends RuntimeException{
    public PlanFactApiRequestException(String message) {
        super(message);
    }
}
