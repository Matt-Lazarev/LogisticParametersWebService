package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.station.StationResponse;
import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;
import com.uraltrans.logisticparamservice.repository.postgres.StationHandbookRepository;
import com.uraltrans.logisticparamservice.service.mapper.StationHandbookMapper;
import com.uraltrans.logisticparamservice.service.utcsrs.RawStationHandbookService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.StationHandbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StationHandbookServiceImpl implements StationHandbookService {
    private final StationHandbookRepository stationHandbookRepository;
    private final RawStationHandbookService rawStationHandbookService;
    private final StationHandbookMapper stationHandbookMapper;

    @Override
    @Transactional
    public void saveAll() {
        prepareNextSave();
        List<Map<String, Object>> rawData = rawStationHandbookService.getAll();
        stationHandbookRepository.saveAll(stationHandbookMapper.mapRawStationHandbookData(rawData));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StationHandbook> getAll() {
        return stationHandbookRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StationResponse> getAllResponses() {
        return stationHandbookMapper.mapToListResponses(stationHandbookRepository.findAll());
    }

    private void prepareNextSave() {
        stationHandbookRepository.truncate();
    }
}
