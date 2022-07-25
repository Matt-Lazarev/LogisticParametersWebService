package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.station.CargoResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.CargoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CargoController {
    private final CargoService cargoService;

    @GetMapping("/result/cargo")
    public List<CargoResponse> getAllResponses(){
        return cargoService.getAllCargoResponses();
    }
}
