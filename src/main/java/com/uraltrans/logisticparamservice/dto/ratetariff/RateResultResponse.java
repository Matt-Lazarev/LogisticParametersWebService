package com.uraltrans.logisticparamservice.dto.ratetariff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RateResultResponse {
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
        private Integer rateVat;
        private BigDecimal rate;
    }
}
