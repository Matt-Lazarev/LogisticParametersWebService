package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.dto.secondempty.SecondEmptyFlightResponse;
import com.uraltrans.logisticparamservice.entity.postgres.SecondEmptyFlight;

import java.util.List;

public interface SecondEmptyFlightService {
    List<SecondEmptyFlight> getAllSecondEmptyFlight();

    List<SecondEmptyFlightResponse> getAllSecondEmptyFlightResponses();
    void saveAllSecondEmptyFlights();
}
