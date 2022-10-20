package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.freewagon.FreeWagonRequest;
import com.uraltrans.logisticparamservice.dto.freewagon.FreeWagonResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightAddressingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FreeWagonController {
    private final FlightAddressingService flightAddressingService;

    @PostMapping("result/freeWagon")
    public List<FreeWagonResponse> getFreeWagonsResponses(@Valid @RequestBody(required=false) FreeWagonRequest freeWagonRequest){
        return flightAddressingService.getAllByFreeWagonRequest(freeWagonRequest);
    }
}
