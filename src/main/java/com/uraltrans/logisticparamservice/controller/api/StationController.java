package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.common.LoadDataResponse;
import com.uraltrans.logisticparamservice.dto.station.StationResponse;
import com.uraltrans.logisticparamservice.service.postgres.abstr.StationHandbookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@Tag(name = "Станции", description = "Операции со станциями")
public class StationController {
    private final StationHandbookService stationHandbookService;

    @GetMapping("/result/station")
    @Operation(summary = "Получение всех станций в описанном формате в API 4.xx")
    public List<StationResponse> getAllResponses(){
        return stationHandbookService.getAllResponses();
    }

    @PostMapping("/station/load")
    @Operation(summary = "Сохранение всех станций")
    public ResponseEntity<LoadDataResponse> loadStations(){
        stationHandbookService.saveAll();
        return ResponseEntity.ok(LoadDataResponse.builder().success("true").message("Данные выгружены").build());
    }
}
