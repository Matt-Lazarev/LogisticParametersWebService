package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.RegionFlightCollapsed;

import java.util.List;

public interface RegionFlightCollapsedService {
    List<RegionFlightCollapsed> getAllRegionFlightCollapsed();
    void saveAllRegionFlightsCollapsed(String logId);
}
