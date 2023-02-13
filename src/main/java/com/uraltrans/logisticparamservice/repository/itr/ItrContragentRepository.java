package com.uraltrans.logisticparamservice.repository.itr;

import java.util.List;
import java.util.Map;

public interface ItrContragentRepository {
    List<Map<String, Object>> getAllContragentsByType(String type);
}
