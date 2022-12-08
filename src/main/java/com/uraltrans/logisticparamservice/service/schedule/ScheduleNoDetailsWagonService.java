package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.NoDetailsWagonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleNoDetailsWagonService {
    private final NoDetailsWagonService noDetailsWagonService;

    public void loadNoDetailsWagons(){
        noDetailsWagonService.saveAllNoDetailsWagon();
    }
}
