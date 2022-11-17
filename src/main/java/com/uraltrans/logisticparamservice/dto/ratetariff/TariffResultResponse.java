package com.uraltrans.logisticparamservice.dto.ratetariff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TariffResultResponse {
    private String id;
    private String distance;
    private String travelTime;
    private String loadingUnloading;
    private String tariffVat;
    private String tariff;
}
