package com.uraltrans.logisticparamservice.service.abstr;

import com.uraltrans.logisticparamservice.dto.idle.LoadIdleDto;
import com.uraltrans.logisticparamservice.dto.idle.LoadingUnloadingDto;
import com.uraltrans.logisticparamservice.dto.idle.UnloadIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.LoadingUnloadingIdle;

import java.util.List;

public interface LoadingUnloadingIdleService {

    List<LoadingUnloadingDto> getAllLoadingUnloadingIdles();
    void deleteAll();
    void saveAll(List<LoadIdleDto> groupCarLoadIdle, List<UnloadIdleDto> groupCarUnloadIdle);
}
