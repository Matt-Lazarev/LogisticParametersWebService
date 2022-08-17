package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.dislocation.DislocationRequest;
import com.uraltrans.logisticparamservice.dto.dislocation.DislocationResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ActualFlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DislocationController {
    private final ActualFlightService actualFlightService;

    @PostMapping("/result/dislocation")
    public List<DislocationResponse> getAllDislocationsByRequest(@Valid @RequestBody(required = false) DislocationRequest request){
        return actualFlightService.getAllByRequest(request);
    }
}
