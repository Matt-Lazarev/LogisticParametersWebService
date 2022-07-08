package com.uraltrans.logisticparamservice.service.impl;

import com.uraltrans.logisticparamservice.entity.postgres.LoadingUnloadingIdle;
import com.uraltrans.logisticparamservice.repository.postgres.LoadingUnloadingIdleRepository;
import com.uraltrans.logisticparamservice.service.abstr.LoadingUnloadingIdleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoadingUnloadingIdleServiceImpl implements LoadingUnloadingIdleService {

    private final LoadingUnloadingIdleRepository loadingUnloadingIdleRepository;

    @Override
    public void saveAll(List<LoadingUnloadingIdle> data) {
        loadingUnloadingIdleRepository.saveAll(data);
    }

    @Override
    public List<LoadingUnloadingIdle> getAllLoadingUnloadingIdles() {
        return loadingUnloadingIdleRepository.findAll();
    }

    @Override
    public void deleteAll() {
        loadingUnloadingIdleRepository.deleteAll();
    }
}
