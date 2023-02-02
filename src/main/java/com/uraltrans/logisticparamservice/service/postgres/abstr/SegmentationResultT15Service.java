package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.SegmentationResultT15;

import java.util.List;

public interface SegmentationResultT15Service {
    void saveAllSegments(String logId);
    List<SegmentationResultT15> getAllSegmentations();
}
