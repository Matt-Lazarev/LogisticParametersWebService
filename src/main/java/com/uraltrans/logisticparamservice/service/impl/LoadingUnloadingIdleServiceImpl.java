package com.uraltrans.logisticparamservice.service.impl;

import com.uraltrans.logisticparamservice.dto.LoadingUnloadingDto;
import com.uraltrans.logisticparamservice.entity.postgres.LoadingUnloadingIdle;
import com.uraltrans.logisticparamservice.mapper.LoadingUnloadingIdleMapper;
import com.uraltrans.logisticparamservice.repository.postgres.LoadingUnloadingIdleRepository;
import com.uraltrans.logisticparamservice.service.abstr.LoadingUnloadingIdleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoadingUnloadingIdleServiceImpl implements LoadingUnloadingIdleService {

    private final LoadingUnloadingIdleMapper loadingUnloadingIdleMapper;
    private final LoadingUnloadingIdleRepository loadingUnloadingIdleRepository;

    @Override
    public void saveAll(List<LoadingUnloadingIdle> data) {
        loadingUnloadingIdleRepository.saveAll(data);
    }

    @Override
    public List<LoadingUnloadingDto> getAllLoadingUnloadingIdles() {
        return loadingUnloadingIdleMapper.mapToListDto(loadingUnloadingIdleRepository.findAll());
    }

    @Override
    public void deleteAll() {
        loadingUnloadingIdleRepository.deleteAll();
    }
}
