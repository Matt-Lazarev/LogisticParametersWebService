package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.ClientOrder;

import java.util.List;

public interface ClientOrderService {
    List<ClientOrder> getAllClientOrders();
    void saveAllClientOrders();
}
