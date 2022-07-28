package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.CargoService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleCargoService {
    private final CargoService cargoService;

    @Scheduled(cron = "0 0 4 * * *")
    public void loadCargos(){
        cargoService.saveAll();
    }
}
