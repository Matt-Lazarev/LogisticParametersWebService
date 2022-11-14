package com.uraltrans.logisticparamservice.dto.station;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StationResponse {
    private String success;
    private String errorText;
    private String idStation;
    private String nameStation;
    private String region;
    private String way;
    private String latitude;
    private String longitude;
}
