package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.dto.cargo.CargoDto;
import com.uraltrans.logisticparamservice.entity.postgres.ClientOrder;

import java.math.BigDecimal;
import java.util.List;

public interface ClientOrderService {
    List<ClientOrder> getAllClientOrders();
    void saveAllClientOrders();

    List<CargoDto> findByStationCodesAndVolume(String sourceStation, BigDecimal volume);
}
