package com.uraltrans.logisticparamservice.service.abstr;

import com.uraltrans.logisticparamservice.dto.idle.LoadIdleDto;
import com.uraltrans.logisticparamservice.dto.idle.FlightIdleDto;
import com.uraltrans.logisticparamservice.dto.idle.UnloadIdleDto;
import com.uraltrans.logisticparamservice.dto.request.LoadDataRequestDto;

import java.util.List;

public interface FlightIdleService {

    List<FlightIdleDto> getAllLoadingUnloadingIdles();
    void saveAll(LoadDataRequestDto dto);
}
