package com.uraltrans.logisticparamservice.controller.mvc;

import com.uraltrans.logisticparamservice.repository.integration.RawDislocationRepository;
import com.uraltrans.logisticparamservice.repository.itr.RawClientOrderRepository;
import com.uraltrans.logisticparamservice.repository.utcsrs.RawFlightProfitRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.*;
import com.uraltrans.logisticparamservice.service.utcsrs.RawFlightProfitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    final ClientOrderService clientOrderService;
    final CargoService cargoService;

    final ActualFlightService service;

    final FlightRequirementService flightRequirementService;

    final FlightProfitService flightProfitService;

    @GetMapping
    public List<?> getAll(){
//        cargoService.saveAll();
//        return cargoService.getAllCargos();
//        clientOrderService.saveAllClientOrders();
        flightProfitService.saveAll();;
        return flightProfitService.getAllFlightProfits();
//        return clientOrderService.getAllClientOrders();
//        service.saveAllActualFlights();
//        return service.getAllActualFlights();
//        flightRequirementService.saveAllFlightRequirements();
//        return flightRequirementService.getAllFlightRequirements();
    }
}
