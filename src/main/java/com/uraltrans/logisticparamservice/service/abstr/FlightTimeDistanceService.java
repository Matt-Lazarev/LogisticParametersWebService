package com.uraltrans.logisticparamservice.service.abstr;

import com.uraltrans.logisticparamservice.dto.time.FlightTimeDistanceRequest;
import com.uraltrans.logisticparamservice.dto.time.FlightTimeDistanceResponse;

import java.util.List;

public interface FlightTimeDistanceService {
    List<FlightTimeDistanceResponse> getTimeDistanceResponses(List<FlightTimeDistanceRequest> requests);
}
