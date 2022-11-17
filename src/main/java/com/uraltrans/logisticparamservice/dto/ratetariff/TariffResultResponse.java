package com.uraltrans.logisticparamservice.dto.ratetariff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TariffResultResponse {
    private String id;
    private Integer distance;
    private Integer travelTime;
    private Integer loadingUnloading;
    private Integer tariffVat;
    private BigDecimal tariff;
}
