package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.planfact.PlanFactRequest;
import com.uraltrans.logisticparamservice.dto.planfact.PlanFactResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightRequirementService;
import com.uraltrans.logisticparamservice.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class FlightRequirementController {
    private final FlightRequirementService flightRequirementService;

    @PostMapping("/result/planfact")
    public ResponseEntity<?> getAllFlightRequirementsByRequest(@Valid @RequestBody(required = false)PlanFactRequest request){
        List<PlanFactResponse> responses = flightRequirementService.getAllFlightRequirementsByRequest(request);
        if(responses.size() == 0){
            return ControllerUtils.getDefaultResponse("рейсы не найдены", request == null ? null : request.getId());
        }

        return ResponseEntity.ok(responses);
    }
}
