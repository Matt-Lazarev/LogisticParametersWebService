package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationAnalysisT15;

import java.util.List;

public interface RegionSegmentationAnalysisT15Service {
    List<RegionSegmentationAnalysisT15> getAllT15Analyses();
    void saveAllRegionSegmentationsAnalysisT15(String logId);
}
