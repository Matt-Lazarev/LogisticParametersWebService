package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.SegmentationAnalysisT14Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleSegmentationT14 {
    private final SegmentationAnalysisT14Service segmentationAnalysisT14Service;

    public void loadSegmentsT14(){
        segmentationAnalysisT14Service.saveAllSegmentsT14();
    }
}
