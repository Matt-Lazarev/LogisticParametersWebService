package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.common.LoadDataResponse;
import com.uraltrans.logisticparamservice.entity.postgres.NoDetailsWagon;
import com.uraltrans.logisticparamservice.service.postgres.abstr.NoDetailsWagonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Вагоны без реквизитов", description = "Операции с вагонами без реквизитов")
public class NoDetailsWagonController {
    private final NoDetailsWagonService noDetailsWagonService;

    @GetMapping("/result/noDetailsWagon")
    @Operation(summary = "Получение всех вагонов без реквизитов")
    public ResponseEntity<List<NoDetailsWagon>> getAllNoDetailsWagons(){
        return ResponseEntity.ok(noDetailsWagonService.getAllNoDetailsWagon());
    }

    @PostMapping("/api/noDetailsWagons")
    @Operation(summary = "Сохранение всех вагонов без реквизитов")
    public ResponseEntity<LoadDataResponse> saveAllNoDetailsWagons(){
        noDetailsWagonService.saveAllNoDetailsWagon();
        return ResponseEntity.ok(LoadDataResponse.builder().success("true").message("Данные выгружены").build());
    }
}
