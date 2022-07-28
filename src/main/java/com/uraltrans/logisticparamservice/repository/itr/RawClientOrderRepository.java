package com.uraltrans.logisticparamservice.repository.itr;

import java.util.List;
import java.util.Map;

public interface RawClientOrderRepository {
    List<Map<String, Object>> getAllOrders(String status, String carType, String dateFrom);

}
