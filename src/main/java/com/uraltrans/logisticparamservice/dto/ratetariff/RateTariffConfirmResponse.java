package com.uraltrans.logisticparamservice.dto.ratetariff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
