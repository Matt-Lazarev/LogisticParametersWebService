package com.uraltrans.logisticparamservice.dto.ratetariff;

import lombok.*;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RateResultResponse {
    private String id;
    private Integer distance;
    private Integer travelTime;
    private Integer loadingUnloading;
    private Integer rateVat;
    private BigDecimal rate;
}
