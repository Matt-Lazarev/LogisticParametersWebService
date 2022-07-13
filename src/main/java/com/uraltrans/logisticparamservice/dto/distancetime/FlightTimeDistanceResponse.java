package com.uraltrans.logisticparamservice.dto.distancetime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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
