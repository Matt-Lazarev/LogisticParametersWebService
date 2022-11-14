package com.uraltrans.logisticparamservice.dto.carinfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
