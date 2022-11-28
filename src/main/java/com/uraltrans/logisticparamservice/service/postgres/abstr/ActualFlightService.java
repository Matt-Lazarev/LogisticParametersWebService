package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.dto.dislocation.DislocationRequest;
import com.uraltrans.logisticparamservice.dto.dislocation.DislocationResponse;
import com.uraltrans.logisticparamservice.entity.postgres.ActualFlight;
import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;

import java.math.BigDecimal;
import java.util.List;

public interface ActualFlightService {
    List<ActualFlight> getAllActualFlights();
    void saveAllActualFlights();
    List<PotentialFlight> getAllPotentialFlights();
    void saveAllPotentialFlights();
    ActualFlight findByStationCodesAndVolume(String sourceStation, String destStation, BigDecimal volume);

    List<DislocationResponse> getAllByRequest(DislocationRequest request);
}
