package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationParameters;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface RegionSegmentationParametersService {
    RegionSegmentationParameters getParameters();
    void updateParameters(RegionSegmentationParameters newParameters);
}
