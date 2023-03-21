package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.idle.FlightIdleDto;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightIdleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/result/load_days")
@RequiredArgsConstructor
@Tag(name = "Простой вагонов", description = "Операции по простою вагонов под выгрузкой и под погрузкой")
public class FlightIdleController {

    private final FlightIdleService flightIdleService;

    @GetMapping
    @Operation(summary = "Получение данных по простою вагонов")
    public List<FlightIdleDto> getAllCarLoadUnloadIdles(){
        return flightIdleService.getAllLoadingUnloadingIdles();
    }
}
