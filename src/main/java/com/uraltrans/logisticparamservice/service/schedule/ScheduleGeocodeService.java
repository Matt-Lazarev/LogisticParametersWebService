package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.GeocodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleGeocodeService {
    private final GeocodeService geocodeService;

    public void loadGeocodes(){
        geocodeService.saveGeocodes();
    }
}
