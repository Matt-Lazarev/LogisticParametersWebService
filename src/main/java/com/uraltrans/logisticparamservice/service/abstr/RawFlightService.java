package com.uraltrans.logisticparamservice.service.abstr;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RawFlightService {
    List<Map<String, Object>> getAllFlightsBetween(LocalDate from, LocalDate to);
}
