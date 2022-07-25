package com.uraltrans.logisticparamservice.controller.mvc;

import com.uraltrans.logisticparamservice.repository.utcsrs.RawFlightProfitRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightProfitService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.StationHandbookService;
import com.uraltrans.logisticparamservice.service.utcsrs.RawFlightProfitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    final FlightProfitService service1;
    final StationHandbookService service2;
    final RawFlightProfitRepository rawRep;

    @GetMapping
    public List<?> getAll(){
        service1.saveAll();
        return service1.getAllFlightProfits();
        //return rawRep.getAllFlightProfits("2022-07-01");
    }
}
