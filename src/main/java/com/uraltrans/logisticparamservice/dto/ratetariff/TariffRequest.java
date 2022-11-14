package com.uraltrans.logisticparamservice.dto.ratetariff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TariffRequest {
    private String id;
    private String departureStation;
    private String destinationStation;
    private String cargo;
    private String wagonType;
    private String volune;
    private String flightType;
    private String url;
}
