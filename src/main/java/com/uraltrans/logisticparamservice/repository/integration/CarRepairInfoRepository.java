package com.uraltrans.logisticparamservice.repository.integration;

import java.util.List;
import java.util.Map;

public interface CarRepairInfoRepository {
    List<Map<String, Object>> getAllCarRepairs(String currentDate);

    Map<String, Object> getCarRepairByDate(String currentDate, Integer carNumber);

    List<Map<String, Object>> getAllCarWheelThicknesses(String currentDate);
}
