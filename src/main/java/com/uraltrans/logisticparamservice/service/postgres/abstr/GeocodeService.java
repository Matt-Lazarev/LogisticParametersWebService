package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.Geocode;

import java.util.List;
import java.util.Map;

public interface GeocodeService {
    Map<String, Geocode> getGeocodesCache();
    void saveGeocodes();

    Long getGeocodeByStationCode(String stationCode);
}
