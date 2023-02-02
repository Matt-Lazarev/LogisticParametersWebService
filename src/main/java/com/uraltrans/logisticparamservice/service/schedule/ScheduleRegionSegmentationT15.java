package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionFlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionSegmentationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleRegionSegmentationT15 {
    private final RegionFlightService regionFlightService;
    private final RegionSegmentationLogService regionSegmentationLogService;

    public void loadRegionSegmentationT15(){
        String logId = regionSegmentationLogService.saveLog();
        regionFlightService.saveAllRegionFlights(logId, true);
    }
}
