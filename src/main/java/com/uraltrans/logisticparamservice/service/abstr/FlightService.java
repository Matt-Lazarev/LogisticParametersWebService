package com.uraltrans.logisticparamservice.service.abstr;

import com.uraltrans.logisticparamservice.dto.LoadDataRequestDto;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;

import java.util.List;

public interface FlightService {
    List<Flight> getAllFlights();
    void saveAllFlights(LoadDataRequestDto loadDataRequestDto);
    void saveLoadingUnloadingIdles();

    void prepareNextSave();
}
