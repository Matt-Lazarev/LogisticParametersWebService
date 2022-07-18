package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.dto.distancetime.FlightTimeDistanceRequest;
import com.uraltrans.logisticparamservice.dto.distancetime.FlightTimeDistanceResponse;
import com.uraltrans.logisticparamservice.dto.request.LoadDataRequestDto;
import com.uraltrans.logisticparamservice.entity.postgres.FlightTimeDistance;

import java.util.List;

public interface FlightTimeDistanceService {

    List<FlightTimeDistance> getAllTimeDistances();
    List<FlightTimeDistanceResponse> getTimeDistanceResponses(List<FlightTimeDistanceRequest> requests);
    void saveAll(LoadDataRequestDto loadDataRequestDto);
}
