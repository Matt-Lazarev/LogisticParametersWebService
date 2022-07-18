package com.uraltrans.logisticparamservice.service.utcsrs;

import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;

import java.util.List;
import java.util.Map;

public interface RawStationHandbookService {
    List<Map<String, Object>> getAll();
}
