package com.uraltrans.logisticparamservice.service.abstr;

import com.uraltrans.logisticparamservice.dto.LoadDataRequestDto;
import com.uraltrans.logisticparamservice.dto.LoadingUnloadingDto;
import com.uraltrans.logisticparamservice.entity.postgres.LoadingUnloadingIdle;

import java.util.List;

public interface LoadingUnloadingIdleService {
    void saveAll(List<LoadingUnloadingIdle> data);
    List<LoadingUnloadingDto> getAllLoadingUnloadingIdles();

    void deleteAll();
}
