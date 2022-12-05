package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.common.LoadDataResponse;
import com.uraltrans.logisticparamservice.dto.station.CargoResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.CargoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CargoController {
    private final CargoService cargoService;

    @GetMapping("/result/cargo")
    public ResponseEntity<List<CargoResponse>> getAllCargoResponses(){
        return ResponseEntity.ok(cargoService.getAllCargoResponses());
    }

    @PostMapping("/api/cargos")
    public ResponseEntity<LoadDataResponse> saveAllCargos(){
        cargoService.saveAll();
        return ResponseEntity.ok(LoadDataResponse.builder().success("true").message("Данные выгружены").build());
    }
}
