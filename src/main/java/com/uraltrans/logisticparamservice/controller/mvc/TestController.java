package com.uraltrans.logisticparamservice.controller.mvc;

import com.uraltrans.logisticparamservice.dto.rate.RateConfirmResponse;
import com.uraltrans.logisticparamservice.dto.rate.RateRequest;
import com.uraltrans.logisticparamservice.service.postgres.abstr.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
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

    final RestTemplate restTemplate;

    @GetMapping
    public List<?> getAll() {
        clientOrderService.saveAllClientOrders();
        service.saveAllActualFlights();
        flightRequirementService.saveAllFlightRequirements();
        service.saveAllPotentialFlights();
        return service.getAllPotentialFlights();
    }

    @GetMapping("/req")
    public RateConfirmResponse[] sendResponse() {
        List<RateRequest> request = Collections.singletonList(RateRequest.builder()
                .id("85620_53590")
                .departureStation("856200")
                .destinationStation("535907")
                .cargo("601009")
                .wagonType("Крытый")
                .volune("138")
                .flightType("Порожний")
                .url("http://10.168.1.6:8081/test")
                .build());

        String url = "http://10.168.0.8/utc_srs/hs/calc/emptyflight";

        return restTemplate.postForObject(url, request, RateConfirmResponse[].class);
    }
}
