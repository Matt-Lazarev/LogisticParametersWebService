package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationCollapsedT15;

import java.util.List;

public interface RegionSegmentationCollapsedT15Service {
    List<RegionSegmentationCollapsedT15> getAllT15Analyses();
    void saveAllRegionSegmentationsAnalysisT15(String logId);
}
