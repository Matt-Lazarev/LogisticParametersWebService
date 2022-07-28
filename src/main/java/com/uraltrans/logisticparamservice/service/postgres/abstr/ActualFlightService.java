package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.ActualFlight;

import java.util.List;

public interface ActualFlightService {
    List<ActualFlight> getAllActualFlights();
    void saveAllActualFlights();
}
