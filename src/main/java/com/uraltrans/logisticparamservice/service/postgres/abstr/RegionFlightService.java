package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.dto.ratetariff.TariffResultResponse;
import com.uraltrans.logisticparamservice.entity.postgres.RegionFlight;

import java.util.List;

public interface RegionFlightService {
    void saveAllRegionFlights(String logId);

    List<RegionFlight> getAllRegionFlights();

    void updateTravelTime(TariffResultResponse response);
}
