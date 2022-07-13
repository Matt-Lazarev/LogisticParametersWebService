package com.uraltrans.logisticparamservice.dto.idle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightIdleDto {
    private String id;
    private String departureStation;
    private String cargo;
    private String wagonType;
    private String volume;
    private Integer loading;
    private Integer unloading;
}
