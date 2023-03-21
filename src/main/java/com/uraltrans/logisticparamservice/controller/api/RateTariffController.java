package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.ratetariff.RateResultResponse;
import com.uraltrans.logisticparamservice.dto.ratetariff.TariffResultResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightAddressingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Расчет ставки и тарифа", description = "Прием ответов от 1С с рассчитанными тарифом и ставков")
public class RateTariffController {
    private final FlightAddressingService flightAddressingService;

    @PostMapping(value = "/tariff", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @Operation(summary = "Прием ответа на запрос по расчету тарифа")
    public void takeTariffResponse(@RequestBody TariffResultResponse response) {
       response.getDetails()
               .forEach(resp -> flightAddressingService.updateTariff(resp.getId(), new BigDecimal(resp.getTariffVat())));
        log.info("responses tariff:\n UID: {} \n body: {}", response.getUid(), response);
    }

    @PostMapping(value = "/rate", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @Operation(summary = "Прием ответа на запрос по расчету ставки")
    public void takeRateResponse(@RequestBody RateResultResponse response) {
        response.getDetails()
                .forEach(resp -> flightAddressingService.updateRate(resp.getId(), new BigDecimal(resp.getRateVat())));
        log.info("responses rate:\n UID: {} \n body: {}", response.getUid(), response);
    }
}
