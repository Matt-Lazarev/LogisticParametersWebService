package com.uraltrans.logisticparamservice.dto.excelt14;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(exclude = {"carsAmount", "stationCode6", "region"})
public class FlightInfo {
    private String departureMonthYear;
    private String stationCode5;
    private String stationCode6;
    private String region;
    private Integer carsAmount;
    private Boolean isUtcFlight;

    public FlightInfo(String departureMonthYear, String stationCode6, String carsAmount, String isUtcFlight) {
        this.departureMonthYear = departureMonthYear;
        this.stationCode5 = stationCode6.substring(0, 5);
        this.stationCode6 = stationCode6;
        this.carsAmount = (int) Double.parseDouble(carsAmount);
        this.isUtcFlight = isUtcFlight.equalsIgnoreCase("ДА");
    }
}
