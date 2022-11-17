package com.uraltrans.logisticparamservice.dto.ratetariff;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RateResultResponse {
    private String id;
    private String distance;
    private String travelTime;
    private String loadingUnloading;
    private String rateVat;
    private String rate;
}
