package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.common.LoadDataResponse;
import com.uraltrans.logisticparamservice.entity.postgres.SegmentationAnalysisT14;
import com.uraltrans.logisticparamservice.service.postgres.abstr.SegmentationAnalysisT14Service;
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
@RequestMapping("/api/segmentation/analysis")
@Tag(name = "Анализ сегментации (t14_analyzed)", description = "Операции по анализу сегментации (t14_analyzed)")
public class SegmentationController {
    private final SegmentationAnalysisT14Service segmentationAnalysisT14Service;

    @GetMapping
    @Operation(summary = "Получение всего анализа сегментации")
    public ResponseEntity<List<SegmentationAnalysisT14>> getAllSegmentsT14(){
        return ResponseEntity.ok(segmentationAnalysisT14Service.getAllSegmentsT14());
    }

    @PostMapping
    @Operation(summary = "Сохранение всего анализа сегментации")
    public ResponseEntity<LoadDataResponse> loadAllSegmentsT14(){
        segmentationAnalysisT14Service.saveAllSegmentsT14();
        return ResponseEntity.ok(LoadDataResponse.builder().success("true").message("Данные выгружены").build());
    }
}
