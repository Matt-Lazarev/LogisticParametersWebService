package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.station.StationResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.StationHandbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StationController {
    private final StationHandbookService stationHandbookService;

    @GetMapping("/result/station")
    public List<StationResponse> getAllResponses(){
        return stationHandbookService.getAllResponses();
    }
}
