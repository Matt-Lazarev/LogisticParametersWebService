package com.uraltrans.logisticparamservice.service.impl;

import com.uraltrans.logisticparamservice.dto.idle.LoadIdleDto;
import com.uraltrans.logisticparamservice.dto.idle.LoadingUnloadingDto;
import com.uraltrans.logisticparamservice.dto.idle.UnloadIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.LoadingUnloadingIdle;
import com.uraltrans.logisticparamservice.mapper.LoadingUnloadingIdleMapper;
import com.uraltrans.logisticparamservice.repository.postgres.LoadingUnloadingIdleRepository;
import com.uraltrans.logisticparamservice.service.abstr.LoadingUnloadingIdleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LoadingUnloadingIdleServiceImpl implements LoadingUnloadingIdleService {

    private final LoadingUnloadingIdleMapper mapper;
    private final LoadingUnloadingIdleRepository loadingUnloadingIdleRepository;

    @Override
    public List<LoadingUnloadingDto> getAllLoadingUnloadingIdles() {
        return mapper.mapToListDto(loadingUnloadingIdleRepository.findAll());
    }

    @Override
    public void deleteAll() {
        loadingUnloadingIdleRepository.deleteAll();
    }

    @Override
    public void saveAll(List<LoadIdleDto> loadIdleDtos, List<UnloadIdleDto> unloadIdleDtos) {
        List<LoadingUnloadingIdle> data = mapper.mapToLoadingUnloadingList(loadIdleDtos, unloadIdleDtos);
        loadingUnloadingIdleRepository.saveAll(data);
    }
}
