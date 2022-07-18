package com.uraltrans.logisticparamservice.repository.utcsrs;

import java.util.List;
import java.util.Map;

public interface RawStationHandbookRepository {
    List<Map<String, Object>> getAllStations();
}
