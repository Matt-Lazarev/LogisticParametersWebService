package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.station.StationResponse;
import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;
import com.uraltrans.logisticparamservice.exception.StationsNotFoundException;
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
    public void saveAll() {
        prepareNextSave();
        List<Map<String, Object>> rawData = rawStationHandbookService.getAll();
        stationHandbookRepository.saveAll(stationHandbookMapper.mapRawStationHandbookData(rawData));
    }

    @Override
    public List<StationHandbook> getAll() {
        return stationHandbookRepository.findAll();
    }

    @Override
    public List<StationResponse> getAllResponses() {
        List<StationResponse> responses = stationHandbookMapper.mapToListResponses(stationHandbookRepository.findAll());
        if(responses.size() == 0){
            throw new StationsNotFoundException("Станции не были найдены. Повторите запрос позже");
        }
        return responses;
    }

    @Override
    public String getRegionByCode6(String code) {
        return stationHandbookRepository.findRegionByCode6(code);
    }

    private void prepareNextSave() {
        stationHandbookRepository.truncate();
    }
}
