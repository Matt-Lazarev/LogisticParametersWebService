package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.ratetariff.RateTariffResultResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightAddressingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/calc")
@RequiredArgsConstructor
public class RateTariffController {
    private final FlightAddressingService flightAddressingService;

    @PostMapping(value = "/tariff", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public void takeTariffResponse(@RequestBody List<RateTariffResultResponse> responses) {
       responses
               .forEach(resp -> flightAddressingService.updateTariff(resp.getId(), BigDecimal.valueOf(resp.getTariffVat())));
        log.info("responses tariff:\n {}", responses);
    }

    @PostMapping(value = "/rate", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public void takeRateResponse(@RequestBody List<RateTariffResultResponse> responses) {
        responses
                .forEach(resp -> flightAddressingService.updateRate(resp.getId(), BigDecimal.valueOf(resp.getRateVat())));
        log.info("responses rate:\n {}", responses);
    }
}
