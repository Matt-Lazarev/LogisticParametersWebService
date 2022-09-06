package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.StationHandbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleStationHandbookService {
    private final StationHandbookService stationHandbookService;

    public void loadStationHandbook(){
        stationHandbookService.saveAll();
    }

    public void updateCoordinates() {
        stationHandbookService.updateCoordinates();
    }
}
