package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.distancetime.FlightTimeDistanceRequest;
import com.uraltrans.logisticparamservice.dto.distancetime.FlightTimeDistanceResponse;
import com.uraltrans.logisticparamservice.entity.postgres.FlightTimeDistance;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightTimeDistanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Время и дистация рейсов", description = "Операции по получению и сохранению времени и дистанции рейсов")
public class FlightTimeDistanceController {
    private final FlightTimeDistanceService flightTimeDistanceService;

    @PostMapping(value = "/result/flight_days", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Сохранение времени и дистации рейсов")
    public List<FlightTimeDistanceResponse> getAllTimeDistancesByRequests(@Valid @RequestBody FlightTimeDistanceRequest request){
        return flightTimeDistanceService.getTimeDistanceResponses(request);
    }

    @GetMapping("/result/flight_days")
    @Operation(summary = "Получение времени и дистанции рейсов")
    public List<FlightTimeDistance> getAllTimeDistances(){
        return flightTimeDistanceService.getAllTimeDistances();
    }
}
