package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.common.LoadDataResponse;
import com.uraltrans.logisticparamservice.entity.postgres.FlightProfit;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightProfitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/profits")
@RequiredArgsConstructor
public class FlightProfitController {
    private final FlightProfitService flightProfitService;

    @GetMapping
    public ResponseEntity<List<FlightProfit>> getAllFlightProfits(){
        return ResponseEntity.ok(flightProfitService.getAllFlightProfits());
    }

    @PostMapping
    public ResponseEntity<LoadDataResponse> saveAllFlightProfits(){
        flightProfitService.saveAll();
        return ResponseEntity.ok(LoadDataResponse.builder().success("true").message("Данные выгружены").build());
    }
}
