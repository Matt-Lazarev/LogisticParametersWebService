package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.RegionFlightSegmentationAnalysisT15;

import java.util.List;

public interface RegionFlightSegmentationAnalysisT15Service {
    List<RegionFlightSegmentationAnalysisT15> getAllT15Analyses();
    void saveAllRegionSegmentationsAnalysisT15(String logId);
}
