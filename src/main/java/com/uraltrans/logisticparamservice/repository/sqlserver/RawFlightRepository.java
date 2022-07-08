package com.uraltrans.logisticparamservice.repository.sqlserver;

import java.util.List;
import java.util.Map;

public interface RawFlightRepository {

    List<Map<String, Object>> getAllFlightsBetween(String from, String to);
}
