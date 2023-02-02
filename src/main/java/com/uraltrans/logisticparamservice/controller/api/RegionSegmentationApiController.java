package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.entity.postgres.SegmentationResultT15;
import com.uraltrans.logisticparamservice.service.postgres.abstr.SegmentationResultT15Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/segmentation")
public class RegionSegmentationApiController {
    private final SegmentationResultT15Service segmentationResultT15Service;

    @GetMapping
    public ResponseEntity<List<SegmentationResultT15>> getSegmentationT15(){
        return ResponseEntity.ok(segmentationResultT15Service.getAllSegmentations());
    }
}
