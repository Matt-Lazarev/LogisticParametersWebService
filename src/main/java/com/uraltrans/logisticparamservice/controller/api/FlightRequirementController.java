package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.planfact.PlanFactRequest;
import com.uraltrans.logisticparamservice.dto.planfact.PlanFactResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightRequirementService;
import com.uraltrans.logisticparamservice.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@Tag(name = "Потребность в рейсах", description = "Операции по потребности рейсов исходя из плана (ClientOrders) и факта (ActualFlights)")
public class FlightRequirementController {
    private final FlightRequirementService flightRequirementService;

    @PostMapping("/result/planfact")
    @Operation(summary = "Получение потребности в рейсах по запросу")
    public ResponseEntity<?> getAllFlightRequirementsByRequest(@Valid @RequestBody(required = false)PlanFactRequest request){
        List<PlanFactResponse> responses = flightRequirementService.getAllFlightRequirementsByRequest(request);
        if(responses.size() == 0){
            return ControllerUtils.getDefaultResponse("рейсы не найдены", request == null ? null : request.getId());
        }

        return ResponseEntity.ok(responses);
    }
}
