package com.uraltrans.logisticparamservice.dto.addressing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressingRequest {
    private String departureStation;
    private String destinationStation;
    private String cargoId;
    private String wagonType;
    private String volume;
}
