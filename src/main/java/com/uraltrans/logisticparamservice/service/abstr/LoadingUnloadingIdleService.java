package com.uraltrans.logisticparamservice.service.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.LoadingUnloadingIdle;

import java.util.List;

public interface LoadingUnloadingIdleService {
    void saveAll(List<LoadingUnloadingIdle> data);
    List<LoadingUnloadingIdle> getAllLoadingUnloadingIdles();

    void deleteAll();
}
