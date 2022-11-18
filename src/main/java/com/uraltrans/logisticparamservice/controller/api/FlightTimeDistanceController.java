package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.distancetime.FlightTimeDistanceRequest;
import com.uraltrans.logisticparamservice.dto.distancetime.FlightTimeDistanceResponse;
import com.uraltrans.logisticparamservice.entity.postgres.FlightTimeDistance;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightTimeDistanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FlightTimeDistanceController {
    private final FlightTimeDistanceService flightTimeDistanceService;

    @PostMapping(value = "/result/flight_days", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FlightTimeDistanceResponse> getAllTimeDistancesByRequests(@Valid @RequestBody FlightTimeDistanceRequest request){
        return flightTimeDistanceService.getTimeDistanceResponses(request);
    }

    @GetMapping("/result/flight_days")
    public List<FlightTimeDistance> getAllTimeDistances(){
        return flightTimeDistanceService.getAllTimeDistances();
    }
}
