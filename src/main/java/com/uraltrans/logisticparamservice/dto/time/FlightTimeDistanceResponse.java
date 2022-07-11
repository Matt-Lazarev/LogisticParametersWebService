package com.uraltrans.logisticparamservice.dto.time;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FlightTimeDistanceResponse {
    private String id;
    private String distance;
    private String travelTime;
    private String success;
    private String errorText;
}
