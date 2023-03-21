package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.freewagon.FreeWagonRequest;
import com.uraltrans.logisticparamservice.dto.freewagon.FreeWagonResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightAddressingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Свободные вагоны", description = "Операции со свободными вагонами")
public class FreeWagonController {
    private final FlightAddressingService flightAddressingService;

    @PostMapping("result/freeWagon")
    @Operation(summary = "Получение свободных вагонов по запросу")
    public List<FreeWagonResponse> getFreeWagonsResponses(@Valid @RequestBody(required=false) FreeWagonRequest freeWagonRequest){
        return flightAddressingService.getAllByFreeWagonRequest(freeWagonRequest);
    }
}
