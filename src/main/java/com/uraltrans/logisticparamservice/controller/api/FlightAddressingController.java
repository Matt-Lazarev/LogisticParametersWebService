package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.addressing.AddressingRequest;
import com.uraltrans.logisticparamservice.dto.addressing.AddressingResponse;
import com.uraltrans.logisticparamservice.dto.common.LoadDataResponse;
import com.uraltrans.logisticparamservice.entity.postgres.FlightAddressing;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ActualFlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ClientOrderService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightAddressingService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightRequirementService;
import com.uraltrans.logisticparamservice.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FlightAddressingController {
    private final ClientOrderService clientOrderService;
    private final ActualFlightService actualFlightService;
    private final FlightRequirementService flightRequirementService;
    private final FlightAddressingService flightAddressingService;

    @PostMapping("/result/address")
    public ResponseEntity<?> getAddressingsByRequest(@Valid @RequestBody(required = false) AddressingRequest request){
        List<AddressingResponse> responses = flightAddressingService.getAllByAddressRequest(request);
        if(responses.size() == 0){
            return ControllerUtils.getDefaultResponse("рейсы не найдены", request == null ? null : request.getId());
        }

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/address")
    public ResponseEntity<List<FlightAddressing>> getAllFlightAddressings(){
        return ResponseEntity.ok(flightAddressingService.getAll());
    }

    @PostMapping("/api/address")
    public ResponseEntity<LoadDataResponse> saveAllFlightAddressings(){
        clientOrderService.saveAllClientOrders();
        actualFlightService.saveAllActualFlights();
        flightRequirementService.saveAllFlightRequirements();
        actualFlightService.saveAllPotentialFlights();
        flightAddressingService.saveAll();
        return ResponseEntity.ok(LoadDataResponse.builder().success("true").message("Данные выгружены").build());
    }
}
