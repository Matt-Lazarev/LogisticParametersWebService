package com.uraltrans.logisticparamservice.dto.ratetariff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RateTariffConfirmResponse {
    private String uid;
    private List<Detail> details;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Detail{
        private String id;
        private String success;
        private String errorText;
    }
}
