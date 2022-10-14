package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.addressing.AddressingRequest;
import com.uraltrans.logisticparamservice.dto.addressing.AddressingResponse;
import com.uraltrans.logisticparamservice.dto.dislocation.DislocationResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightAddressingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FlightAddressingController {
    private final FlightAddressingService flightAddressingService;

    @PostMapping("/result/address")
    public ResponseEntity<?> getAddressingsByRequest(@Valid @RequestBody(required = false) AddressingRequest request){
        List<AddressingResponse> responses = flightAddressingService.getAllByRequest(request);
        if(responses.size() == 0){
            Map<String, String> errors = new LinkedHashMap<>();
            errors.put("success", "false");
            errors.put("errorText", "рейсы не найдены");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
        }

        return ResponseEntity.ok(responses);
    }
}
