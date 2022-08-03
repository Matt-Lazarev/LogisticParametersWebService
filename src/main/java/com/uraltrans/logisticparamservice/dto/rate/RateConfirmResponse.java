package com.uraltrans.logisticparamservice.dto.rate;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RateConfirmResponse {
    private String id;
    private String success;
    private String errorText;
}
