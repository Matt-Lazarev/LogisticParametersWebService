package com.uraltrans.logisticparamservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AddressApiRequestException extends RuntimeException {
    public AddressApiRequestException(String message) {
        super(message);
    }
}
