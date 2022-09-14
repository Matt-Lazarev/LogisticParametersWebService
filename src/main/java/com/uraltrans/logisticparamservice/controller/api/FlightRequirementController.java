package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.planfact.PlanFactRequest;
import com.uraltrans.logisticparamservice.dto.planfact.PlanFactResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class FlightRequirementController {
    private final FlightRequirementService flightRequirementService;

    @PostMapping("/result/planfact")
    public List<PlanFactResponse> getAllFlightRequirementsByRequest(@Valid @RequestBody(required = false)PlanFactRequest request){
        return flightRequirementService.getAllFlightRequirementsByRequest(request);
    }
}
