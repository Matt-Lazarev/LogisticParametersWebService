package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.common.LoadDataResponse;
import com.uraltrans.logisticparamservice.dto.secondempty.SecondEmptyFlightResponse;
import com.uraltrans.logisticparamservice.entity.postgres.SecondEmptyFlight;
import com.uraltrans.logisticparamservice.service.postgres.abstr.SecondEmptyFlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Вторые порожние рейсы", description = "Операции со вторыми порожними рейсами")
public class SecondEmptyFlightController {
    private final SecondEmptyFlightService secondEmptyFlightService;

    @GetMapping("/api/secondemptyflights")
    @Operation(summary = "Получение вторых порожних рейсов")
    public ResponseEntity<List<SecondEmptyFlight>> getAllSecondEmptyFlights(){
        return ResponseEntity.ok(secondEmptyFlightService.getAllSecondEmptyFlight());
    }

    @PostMapping("/api/secondemptyflights")
    @Operation(summary = "Сохранение вторых порожних рейсов")
    public ResponseEntity<LoadDataResponse> saveAllSecondEmptyFlights(){
        secondEmptyFlightService.saveAllSecondEmptyFlights();
        return ResponseEntity.ok(LoadDataResponse.builder().success("true").message("Данные выгружены").build());
    }

    @GetMapping("/result/second_empty")
    @Operation(summary = "Получение вторых порожних рейсов в заданном по описанному формату в API 4.xx")
    public ResponseEntity<List<SecondEmptyFlightResponse>> getAllSecondEmptyFlightResponses(){
        return ResponseEntity.ok(secondEmptyFlightService.getAllSecondEmptyFlightResponses());
    }
}
