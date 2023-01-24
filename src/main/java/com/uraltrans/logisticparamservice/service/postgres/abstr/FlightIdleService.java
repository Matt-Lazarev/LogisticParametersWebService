package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.dto.idle.FlightIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;

import java.util.List;
import java.util.Optional;

public interface FlightIdleService {
    List<FlightIdleDto> getAllLoadingUnloadingIdles();
    void saveAll(LoadParameters dto);

    Optional<Double> getLoadIdleByStationCode(String stationCode);

    Optional<Double> getUnloadIdleByStationCode(String stationCode);
}
