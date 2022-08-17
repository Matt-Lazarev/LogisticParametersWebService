package com.uraltrans.logisticparamservice.dto.planfact;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrdersDto {
    private Integer requirementOrders;
    private BigDecimal utRate;

    public OrdersDto(Long requirementOrders, Double utRate) {
        this.requirementOrders = requirementOrders.intValue();
        this.utRate = utRate != null
                ? new BigDecimal(utRate).setScale(2, RoundingMode.HALF_UP)
                : null;
    }
}
