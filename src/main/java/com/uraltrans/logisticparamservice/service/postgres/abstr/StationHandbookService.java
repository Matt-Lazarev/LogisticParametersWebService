package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.dto.station.StationResponse;
import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;

import java.util.List;

public interface StationHandbookService {
    void saveAll();
    List<StationHandbook> getAll();

    List<StationResponse> getAllResponses();

    String getRegionByCode6(String code);
}
