package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.common.LoadDataResponse;
import com.uraltrans.logisticparamservice.entity.postgres.SecondEmptyFlight;
import com.uraltrans.logisticparamservice.service.postgres.abstr.SecondEmptyFlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/secondemptyflights")
@RequiredArgsConstructor
public class SecondEmptyFlightController {

    private final SecondEmptyFlightService secondEmptyFlightService;

    @GetMapping
    public ResponseEntity<List<SecondEmptyFlight>> getAllSecondEmptyFlights(){
        return ResponseEntity.ok(secondEmptyFlightService.getAllSecondEmptyFlight());
    }

    @PostMapping
    public ResponseEntity<LoadDataResponse> saveAllSecondEmptyFlights(){
        secondEmptyFlightService.saveAllSecondEmptyFlights();
        return ResponseEntity.ok(LoadDataResponse.builder().success("true").message("Данные выгружены").build());
    }
}
