package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.common.LoadDataResponse;
import com.uraltrans.logisticparamservice.entity.postgres.SegmentationAnalysisT14;
import com.uraltrans.logisticparamservice.service.postgres.abstr.SegmentationAnalysisT14Service;
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
public class SegmentationController {
    private final SegmentationAnalysisT14Service segmentationAnalysisT14Service;

    @GetMapping
    public ResponseEntity<List<SegmentationAnalysisT14>> getAllSegmentsT14(){
        return ResponseEntity.ok(segmentationAnalysisT14Service.getAllSegmentsT14());
    }

    @PostMapping
    public ResponseEntity<LoadDataResponse> loadAllSegmentsT14(){
        segmentationAnalysisT14Service.saveAllSegmentsT14();
        return ResponseEntity.ok(LoadDataResponse.builder().success("true").message("Данные выгружены").build());
    }
}
