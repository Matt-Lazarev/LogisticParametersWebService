package com.uraltrans.logisticparamservice.dto.distancetime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightTimeDistanceRequest {
    private String id;
    private String departureStation;
    private String destinationStation;
    private String typeFlight;
}
