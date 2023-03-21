package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.dislocation.DislocationRequest;
import com.uraltrans.logisticparamservice.dto.dislocation.DislocationResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ActualFlightService;
import com.uraltrans.logisticparamservice.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Дислокация 1С", description = "Операции по дислокации вагонов из 1С (integration)")
public class DislocationController {
    private final ActualFlightService actualFlightService;

    @PostMapping("/result/dislocation")
    @Operation(summary = "Получение дислокации по запросу")
    public ResponseEntity<?> getAllDislocationsByRequest(@Valid @RequestBody(required = false) DislocationRequest request){
        List<DislocationResponse> responses = actualFlightService.getAllByRequest(request);
        if(responses.size() == 0){
            return ControllerUtils.getDefaultResponse("рейсы не найдены", request == null ? null : request.getId());
        }

        return ResponseEntity.ok(responses);
    }
}
