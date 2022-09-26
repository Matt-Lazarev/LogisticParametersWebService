package com.uraltrans.logisticparamservice.repository.itr;

import java.util.List;
import java.util.Map;

public interface RawSecondEmptyFlightRepository {
    List<Map<String, Object>> getRawSecondEmptyFlightData(String from, String to);
}
