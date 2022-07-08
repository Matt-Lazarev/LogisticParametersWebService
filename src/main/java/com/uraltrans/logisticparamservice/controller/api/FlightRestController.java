package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.service.abstr.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightRestController {

    private final FlightService flightService;

    @GetMapping("/raw")
    public List<Flight> getAllFlights(){
        return flightService.getAllFlights();
    }
}
