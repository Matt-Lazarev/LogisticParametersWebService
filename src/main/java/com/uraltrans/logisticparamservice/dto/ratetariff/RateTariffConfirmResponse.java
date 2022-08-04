package com.uraltrans.logisticparamservice.dto.ratetariff;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RateTariffConfirmResponse {
    private String id;
    private String success;
    private String errorText;
}
