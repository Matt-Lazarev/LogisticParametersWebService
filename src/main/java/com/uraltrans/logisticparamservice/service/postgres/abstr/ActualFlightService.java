package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.ActualFlight;
import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;

import java.util.List;

public interface ActualFlightService {
    List<ActualFlight> getAllActualFlights();
    void saveAllActualFlights();
    List<PotentialFlight> getAllPotentialFlights();
    void saveAllPotentialFlights();
}
