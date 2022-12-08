package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.common.LoadDataResponse;
import com.uraltrans.logisticparamservice.entity.postgres.NoDetailsWagon;
import com.uraltrans.logisticparamservice.service.postgres.abstr.NoDetailsWagonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoDetailsWagonController {
    private final NoDetailsWagonService noDetailsWagonService;

    @GetMapping("/result/noDetailsWagon")
    public ResponseEntity<List<NoDetailsWagon>> getAllNoDetailsWagons(){
        return ResponseEntity.ok(noDetailsWagonService.getAllNoDetailsWagon());
    }

    @PostMapping("/api/noDetailsWagons")
    public ResponseEntity<LoadDataResponse> saveAllNoDetailsWagons(){
        noDetailsWagonService.saveAllNoDetailsWagon();
        return ResponseEntity.ok(LoadDataResponse.builder().success("true").message("Данные выгружены").build());
    }
}
