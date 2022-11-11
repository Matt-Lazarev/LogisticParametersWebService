package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.dislocation.DislocationRequest;
import com.uraltrans.logisticparamservice.dto.dislocation.DislocationResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ActualFlightService;
import com.uraltrans.logisticparamservice.utils.ControllerUtils;
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
public class DislocationController {
    private final ActualFlightService actualFlightService;

    @PostMapping("/result/dislocation")
    public ResponseEntity<?> getAllDislocationsByRequest(@Valid @RequestBody(required = false) DislocationRequest request){
        List<DislocationResponse> responses = actualFlightService.getAllByRequest(request);
        if(responses.size() == 0){
            return ControllerUtils.getDefaultResponse("рейсы не найдены", request == null ? null : request.getId());
        }

        return ResponseEntity.ok(responses);
    }
}
