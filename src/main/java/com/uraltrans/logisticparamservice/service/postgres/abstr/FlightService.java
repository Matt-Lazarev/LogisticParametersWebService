package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.dto.idle.LoadIdleDto;
import com.uraltrans.logisticparamservice.dto.idle.UnloadIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;

import java.util.List;

public interface FlightService {
    List<Flight> getAllFlights();

    List<LoadIdleDto> getGroupedCarLoadIdle();

    List<UnloadIdleDto> getGroupCarUnloadIdle();

    void saveAllFlights(LoadParameters loadParameters);

    void saveAll(List<Flight> flights);
}
