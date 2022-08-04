package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.FlightAddressing;

import java.util.List;

public interface FlightAddressingService {
    List<FlightAddressing> getAll();
    void saveAll();
}
