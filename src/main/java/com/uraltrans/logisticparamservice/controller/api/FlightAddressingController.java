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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Адресация вагонов", description = "Расчет и получение адресации вагонов")
public class FlightAddressingController {
    private final ClientOrderService clientOrderService;
    private final ActualFlightService actualFlightService;
    private final FlightRequirementService flightRequirementService;
    private final FlightAddressingService flightAddressingService;

    @PostMapping("/result/address")
    @Operation(summary = "Получение адресации вагонов по запросу")
    public ResponseEntity<?> getAddressingsByRequest(@Valid @RequestBody(required = false) AddressingRequest request){
        List<AddressingResponse> responses = flightAddressingService.getAllByAddressRequest(request);
        if(responses.size() == 0){
            return ControllerUtils.getDefaultResponse("рейсы не найдены", request == null ? null : request.getId());
        }

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/address")
    @Operation(summary = "Получение всей адресации вагонов")
    public ResponseEntity<List<FlightAddressing>> getAllFlightAddressings(){
        return ResponseEntity.ok(flightAddressingService.getAll());
    }

    @PostMapping("/api/address")
    @Operation(summary = "Расчет (сохранение) адресации вагонов")
    public ResponseEntity<LoadDataResponse> saveAllFlightAddressings(){
        clientOrderService.saveAllClientOrders();
        actualFlightService.saveAllActualFlights();
        flightRequirementService.saveAllFlightRequirements();
        actualFlightService.saveAllPotentialFlights();
        flightAddressingService.saveAll();
        return ResponseEntity.ok(LoadDataResponse.builder().success("true").message("Данные выгружены").build());
    }
}
