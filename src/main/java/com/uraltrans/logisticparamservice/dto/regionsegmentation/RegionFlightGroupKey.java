package com.uraltrans.logisticparamservice.dto.regionsegmentation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RegionFlightGroupKey {
    private BigDecimal volume;
    private String sourceRegion;
    private String destRegion;
    private String type;
}
