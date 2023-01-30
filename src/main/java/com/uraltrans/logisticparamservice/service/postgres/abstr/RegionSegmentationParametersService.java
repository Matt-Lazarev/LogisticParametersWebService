package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationParameters;


public interface RegionSegmentationParametersService {
    RegionSegmentationParameters getParameters();
    void updateParameters(RegionSegmentationParameters newParameters);
}
