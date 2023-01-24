package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.ratetariff.TariffResultResponse;
import com.uraltrans.logisticparamservice.entity.postgres.TariffResponse;
import com.uraltrans.logisticparamservice.repository.postgres.TariffResponseRepository;
import com.uraltrans.logisticparamservice.service.mapper.mapstruct.TariffMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.TariffResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffResponseServiceImpl implements TariffResponseService {
    private final TariffResponseRepository tariffResponseRepository;
    private final TariffMapper tariffMapper;

    @Override
    public List<TariffResponse> getAllTariffResponses() {
        return tariffResponseRepository.findAll();
    }

    @Override
    public void saveAllTariffResponses(TariffResultResponse tariffResultResponse) {
        prepareNextSave();
        tariffResponseRepository.saveAll(tariffMapper.mapToTariffResponsesList(tariffResultResponse));
    }

    private void prepareNextSave(){
        tariffResponseRepository.deleteAll();
    }
}
