package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.SecondEmptyFlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightRestController {

    private final FlightService flightService;
    private final SecondEmptyFlightService secondEmptyFlightService;

    @GetMapping("/raw")
    public List<Flight> getAllFlights(){
        return flightService.getAllFlights();
    }

    @PostMapping("/second-empty-flight")
    public ResponseEntity<?> saveSecondEmptyFlights(){
        secondEmptyFlightService.saveAllSecondEmptyFlights();
        return ResponseEntity.ok().build();
    }
}
