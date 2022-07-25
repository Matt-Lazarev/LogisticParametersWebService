package com.uraltrans.logisticparamservice.dto.station;

import lombok.*;

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
