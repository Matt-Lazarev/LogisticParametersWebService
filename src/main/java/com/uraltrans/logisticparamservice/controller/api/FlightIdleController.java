package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.idle.FlightIdleDto;
import com.uraltrans.logisticparamservice.service.abstr.FlightIdleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/result/load_days")
@RequiredArgsConstructor
public class FlightIdleController {

    private final FlightIdleService flightIdleService;

    @GetMapping
    public List<FlightIdleDto> getAllCarLoadUnloadIdles(){
        return flightIdleService.getAllLoadingUnloadingIdles();
    }
}
