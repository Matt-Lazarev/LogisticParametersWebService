package com.uraltrans.logisticparamservice.dto.cargo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CargoDto {
    private String cargoCode;
    private BigDecimal utRate;

    public CargoDto(Double utRate) {
        this.utRate = new BigDecimal(utRate).setScale(2, RoundingMode.HALF_UP);
    }
}
