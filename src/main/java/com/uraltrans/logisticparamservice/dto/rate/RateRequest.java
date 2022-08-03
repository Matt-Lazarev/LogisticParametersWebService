package com.uraltrans.logisticparamservice.dto.rate;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RateRequest {
    private String id;
    private String departureStation;
    private String destinationStation;
    private String cargo;
    private String wagonType;
    private String volune;
    private String flightType;
    private String url;
}
