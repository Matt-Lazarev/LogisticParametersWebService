package com.uraltrans.logisticparamservice.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public class ControllerUtils {
    public static ResponseEntity<?> getDefaultResponse(String message, String id){
        Map<String, String> errors = new LinkedHashMap<>();
        if(id != null){
            errors.put("id", id);
        }
        errors.put("success", "false");
        errors.put("errorText", message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }
}
