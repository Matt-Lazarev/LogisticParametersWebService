package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ratetariff")
@RequiredArgsConstructor
public class RateTariffStateController {
    private final LoadParameterService loadParameterService;

    @GetMapping("/change")
    public ResponseEntity<?> changeState(){
        LoadParameters parameters = loadParameterService.getLoadParameters();
        parameters.setRateTariffState(!parameters.getRateTariffState());
        loadParameterService.updateLoadParameters(parameters);
        return ResponseEntity.ok().build();
    }
}
