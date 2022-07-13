package com.uraltrans.logisticparamservice.dto.distancetime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FlightTimeDistanceRequest {
    private String id;
    private String departureStation;
    private String destinationStation;
    private String flightType;
}
