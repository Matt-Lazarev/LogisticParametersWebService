package com.uraltrans.logisticparamservice.dto.idle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class LoadIdleDto {

    private BigDecimal volume;
    private String cargo;
    private String cargoCode6;
    private String sourceStation;
    private String sourceStationCode;
    private Integer carLoadIdleDays;
    private String carType;

    public LoadIdleDto(BigDecimal volume, String cargo, String cargoCode6, String sourceStation,
                       String sourceStationCode, String carType, Double carLoadIdleDays) {
        this.volume = volume;
        this.cargo = cargo;
        this.cargoCode6 = cargoCode6;
        this.sourceStation = sourceStation;
        this.sourceStationCode = sourceStationCode;
        this.carType = carType;
        this.carLoadIdleDays = carLoadIdleDays != null ? (int) Math.ceil(carLoadIdleDays) : null;
    }
}
