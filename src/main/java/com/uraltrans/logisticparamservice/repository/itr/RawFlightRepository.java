package com.uraltrans.logisticparamservice.repository.itr;

import java.util.List;
import java.util.Map;

public interface RawFlightRepository {
    List<Map<String, Object>> getAllFlightsBetween(String[][] fromToDatePairs);
}
