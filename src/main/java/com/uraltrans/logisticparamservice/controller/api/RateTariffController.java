package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.ratetariff.RateTarrifResultResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightAddressingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/calc")
@RequiredArgsConstructor
public class RateTariffController {
    private final FlightAddressingService flightAddressingService;

    @PostMapping(value = "/tariff", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public void takeTariffResponse(@RequestBody List<RateTarrifResultResponse> responses) {
       responses
               .forEach(resp -> flightAddressingService.updateTariff(resp.getId(), resp.getTariff()));
        System.err.println("response");
    }

    @PostMapping(value = "/rate", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public void takeRateResponse(@RequestBody List<RateTarrifResultResponse> responses) {
        responses
                .forEach(resp -> flightAddressingService.updateRate(resp.getId(), resp.getRate()));
        System.err.println("response");
    }
}
