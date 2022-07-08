package com.uraltrans.logisticparamservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class UnloadIdleDto {

    private BigDecimal volume;
    private String cargoCode6;
    private String destStation;
    private String destStationCode;
    private Integer carUnloadIdleDays;
    private String carType;

    public UnloadIdleDto(BigDecimal volume, String cargoCode6, String destStation,
                         String destStationCode, String carType, Double carUnloadIdleDays) {
        this.volume = volume;
        this.cargoCode6 = cargoCode6;
        this.destStation = destStation;
        this.destStationCode = destStationCode;
        this.carType = carType;
        this.carUnloadIdleDays = carUnloadIdleDays != null ?  (int) Math.ceil(carUnloadIdleDays) : null;
    }
}
