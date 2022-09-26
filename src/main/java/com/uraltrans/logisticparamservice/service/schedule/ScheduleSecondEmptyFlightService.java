package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.SecondEmptyFlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleSecondEmptyFlightService {
    private final SecondEmptyFlightService secondEmptyFlightService;

    public void loadSecondEmptyFlights(){
        secondEmptyFlightService.saveAllSecondEmptyFlights();
    }
}
