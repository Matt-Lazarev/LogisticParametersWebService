package com.uraltrans.logisticparamservice.dto.ratetariff;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RateRequest {
    private String id;
    private String departureStation;
    private String destinationStation;
    private String cargo;
    private String wagonType;
    private String volune;
    private String dateto;
    private String datefrom;
}
