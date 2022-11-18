package com.uraltrans.logisticparamservice.dto.ratetariff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
public class RateTariffConfirmResponse {
    private String uid;
    private List<Detail> details;

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter @Getter @ToString
    public static class Detail{
        private String id;
        private String success;
        private String errorText;
    }
}
