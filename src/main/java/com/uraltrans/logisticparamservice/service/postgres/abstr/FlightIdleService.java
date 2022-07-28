package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.dto.idle.FlightIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;

import java.util.List;

public interface FlightIdleService {

    List<FlightIdleDto> getAllLoadingUnloadingIdles();
    void saveAll(LoadParameters dto);
}
