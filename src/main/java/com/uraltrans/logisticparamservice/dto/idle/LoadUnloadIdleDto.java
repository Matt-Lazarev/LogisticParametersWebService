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
public class LoadUnloadIdleDto {

    private String sourceStation;
    private String sourceStationCode;
    private String destStation;
    private String destStationCode;
    private BigDecimal volume;
    private String cargo;
    private String cargoCode6;
    private Integer carLoadIdleDays;
    private Integer carUnloadIdleDays;

    public LoadUnloadIdleDto(BigDecimal volume, String cargo, String cargoCode6,
                             String sourceStation, String sourceStationCode,
                             String destStation, String destStationCode,
                             Double carLoadIdleDays, Double carUnloadIdleDays) {
        this.volume = volume;
        this.cargo = cargo;
        this.cargoCode6 = cargoCode6;
        this.sourceStation = sourceStation;
        this.sourceStationCode = sourceStationCode;
        this.destStation = destStation;
        this.destStationCode = destStationCode;
        this.carLoadIdleDays = carLoadIdleDays != null ? (int) Math.ceil(carLoadIdleDays) : null;
        this.carUnloadIdleDays = carUnloadIdleDays != null ? (int) Math.ceil(carUnloadIdleDays) : null;
    }
}
