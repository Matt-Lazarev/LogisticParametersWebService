package com.uraltrans.logisticparamservice.dto.carinfo;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarThicknessDto {
    private String carNumber;
    private BigDecimal thicknessWheel;
    private BigDecimal thicknessComb;
}
