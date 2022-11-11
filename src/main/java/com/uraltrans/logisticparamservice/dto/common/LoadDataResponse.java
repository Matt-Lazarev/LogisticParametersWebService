package com.uraltrans.logisticparamservice.dto.common;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoadDataResponse {
    private String success;
    private String message;
}
