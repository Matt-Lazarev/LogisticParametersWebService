package com.uraltrans.logisticparamservice.service.abstr;

import com.uraltrans.logisticparamservice.dto.request.LoadDataRequestDto;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;

import java.util.List;

public interface FlightIdleService {
    List<Flight> getAllFlights();
    void saveAllFlights(LoadDataRequestDto loadDataRequestDto);
}
