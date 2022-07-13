package com.uraltrans.logisticparamservice.service.abstr;

import com.uraltrans.logisticparamservice.dto.idle.LoadIdleDto;
import com.uraltrans.logisticparamservice.dto.idle.UnloadIdleDto;
import com.uraltrans.logisticparamservice.dto.request.LoadDataRequestDto;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;

import java.util.List;

public interface FlightService {
    List<Flight> getAllFlights();

    List<LoadIdleDto> getGroupedCarLoadIdle();

    List<UnloadIdleDto> getGroupCarUnloadIdle();

    void saveAllFlights(LoadDataRequestDto loadDataRequestDto);

    void saveAll(List<Flight> flights);
}
