package com.uraltrans.logisticparamservice.dto.cargo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CargoDto {
    private String cargoCode;
    private BigDecimal utRate;
}
