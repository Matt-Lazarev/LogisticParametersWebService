package com.uraltrans.logisticparamservice.repository.utcsrs;

import java.util.List;
import java.util.Map;

public interface RawFlightProfitRepository {
    List<Map<String, Object>> getAllFlightProfits(String fromDate);
}
