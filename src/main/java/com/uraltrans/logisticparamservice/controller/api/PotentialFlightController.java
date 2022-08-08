package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.ratetariff.RateTariffResultResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rate")
public class PotentialFlightController {

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public void takeResponse(@RequestBody List<RateTariffResultResponse> body){
        System.err.println(body);
    }
}
