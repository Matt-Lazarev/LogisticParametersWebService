package com.uraltrans.logisticparamservice.dto.regionsegmentation;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class LoadedFlight {
    private LocalDate departureDate;
    private BigDecimal volume;
    private String sourceStation;
    private String destStation;
    private String cargo;
    private BigDecimal rate;
    private Integer flightsAmount;
    private String sourceRegion;
    private String sourceStationCode;
    private String destStationCode;
    private String destRegion;
    private String type;
    private Integer travelDays;
    private Integer sourceStationLoadIdleDays;
    private Integer sourceStationUnloadIdleDays;
    private Integer destStationLoadIdleDays;
    private Integer destStationUnloadIdleDays;

    public LoadedFlight(String departureDate, String volume, String sourceStation,
                        String destStation, String cargo, String rate, String flightsAmount) {
        this.departureDate = LocalDate.parse(departureDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        this.volume = new BigDecimal(volume.replace(",", "."));
        this.sourceStation = sourceStation;
        this.destStation = destStation;
        this.cargo = cargo;
        this.rate = new BigDecimal(rate.replace(",", ".").replace(" ", ""));
        this.flightsAmount = Integer.parseInt(flightsAmount);
        this.type = "Груженый";
    }

    public LoadedFlight(BigDecimal volume, String sourceStation, String destStation, BigDecimal rate, Integer flightsAmount) {
        this.volume = volume;
        this.sourceStation = sourceStation;
        this.destStation = destStation;
        this.rate = rate;
        this.flightsAmount = flightsAmount;
        this.type = "Груженый";
    }
}
