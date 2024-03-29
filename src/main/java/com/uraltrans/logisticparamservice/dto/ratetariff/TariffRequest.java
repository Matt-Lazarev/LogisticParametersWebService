package com.uraltrans.logisticparamservice.dto.ratetariff;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TariffRequest {
    private String id;
    private String departureStation;
    private String destinationStation;
    private String cargo;
    private String wagonType;
    private String volune;
    private String flightType;
}
