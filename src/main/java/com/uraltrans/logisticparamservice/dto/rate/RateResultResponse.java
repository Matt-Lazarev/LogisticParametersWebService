package com.uraltrans.logisticparamservice.dto.rate;

import lombok.*;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RateResultResponse {

    private String id;
    private String status;
    private Integer distance;
    private Integer travelTime;
    private Integer loadingUnloading;
    private Integer travelTimeRoute;
    private Integer loadingUnloadingRoute;
    private Integer turnover;
    private Integer tariffVat;
    private BigDecimal tariff;
    private BigDecimal rate;
    private Integer rateVat;
    private Integer profit;
}
