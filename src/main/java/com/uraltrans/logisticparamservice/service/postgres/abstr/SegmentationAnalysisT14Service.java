package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.SegmentationAnalysisT14;

import java.util.List;

public interface SegmentationAnalysisT14Service {
    void saveAllSegmentsT14();
    List<SegmentationAnalysisT14> getAllSegmentsT14();
}
