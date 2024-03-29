package com.uraltrans.logisticparamservice.dto.ratetariff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TariffResultResponse {
    private String uid;
    private List<Detail> details;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Detail {
        private String id;
        private Integer distance;
        private Integer travelTime;
        private Integer loadingUnloading;
        private Integer tariffVat;
        private BigDecimal tariff;
    }
}
