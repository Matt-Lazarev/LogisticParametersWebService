package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.dto.addressing.AddressingRequest;
import com.uraltrans.logisticparamservice.dto.addressing.AddressingResponse;
import com.uraltrans.logisticparamservice.dto.freewagon.FreeWagonRequest;
import com.uraltrans.logisticparamservice.dto.freewagon.FreeWagonResponse;
import com.uraltrans.logisticparamservice.entity.postgres.FlightAddressing;

import java.math.BigDecimal;
import java.util.List;


public interface FlightAddressingService {
    List<FlightAddressing> getAll();
    void saveAll();

    void updateTariff(String id, BigDecimal tariff);

    void updateRate(String id, BigDecimal rate);

    List<AddressingResponse> getAllByAddressRequest(AddressingRequest request);
    List<FreeWagonResponse> getAllByFreeWagonRequest(FreeWagonRequest request);
}
