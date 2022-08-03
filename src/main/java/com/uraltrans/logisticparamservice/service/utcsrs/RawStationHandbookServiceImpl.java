package com.uraltrans.logisticparamservice.service.utcsrs;

import com.uraltrans.logisticparamservice.repository.utcsrs.RawStationHandbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RawStationHandbookServiceImpl implements RawStationHandbookService {
    private final RawStationHandbookRepository rawStationHandbookRepository;

    @Override
    public  List<Map<String, Object>> getAll() {
        return rawStationHandbookRepository.getAllStations();
    }
}
