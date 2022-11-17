package com.uraltrans.logisticparamservice.dto.ratetariff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RateResultResponse {
    private String id;
    private Integer distance;
    private Integer travelTime;
    private Integer loadingUnloading;
    private Integer rateVat;
    private BigDecimal rate;
}
