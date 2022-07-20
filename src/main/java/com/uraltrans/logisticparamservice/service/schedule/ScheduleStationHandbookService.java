package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.StationHandbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleStationHandbookService {
    private final StationHandbookService stationHandbookService;

    @Scheduled(cron = "${params.nextDataLoadTimeCron}")
    public void loadStationHandbook(){
        stationHandbookService.saveAll();
    }
}
