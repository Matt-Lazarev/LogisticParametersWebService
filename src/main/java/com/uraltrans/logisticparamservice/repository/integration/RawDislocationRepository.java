package com.uraltrans.logisticparamservice.repository.integration;

import java.util.List;
import java.util.Map;

public interface RawDislocationRepository {
    List<Map<String, Object>> getAllDislocations(String dislocationDate);
}
