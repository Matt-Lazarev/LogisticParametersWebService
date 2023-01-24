package com.uraltrans.logisticparamservice.dto.regionsegmentation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class FlightGroupKey {
    private BigDecimal volume;
    private String sourceStation;
    private String destStation;
}
