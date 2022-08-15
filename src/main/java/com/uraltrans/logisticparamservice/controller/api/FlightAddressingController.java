package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.addressing.AddressingRequest;
import com.uraltrans.logisticparamservice.dto.addressing.AddressingResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightAddressingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FlightAddressingController {
    private final FlightAddressingService flightAddressingService;

    @PostMapping("/result/address")
    public List<AddressingResponse> getAddressingsByRequest(@RequestBody(required = false) AddressingRequest request){
        return flightAddressingService.getAllByRequest(request);
    }
}
