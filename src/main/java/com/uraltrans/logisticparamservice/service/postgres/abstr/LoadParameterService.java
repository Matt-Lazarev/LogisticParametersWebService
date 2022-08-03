package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface LoadParameterService {
    LoadParameters getLoadParameters();
    void updateLoadParameters(LoadParameters newParameters);
    List<String> getManagers();
    LocalTime getNextDataLoadTime();

    void updateNextDataLoad(LocalDateTime nextExecution);
}
