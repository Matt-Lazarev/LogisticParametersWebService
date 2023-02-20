package com.uraltrans.logisticparamservice.repository.utcsrs;

import java.util.List;
import java.util.Map;

public interface RawCargoRepository {
    List<Map<String, Object>> getAllCargos();
}
