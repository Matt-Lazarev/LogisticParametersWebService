package com.uraltrans.logisticparamservice.dto.regionsegmentation;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class EmptyFlight {
    private LocalDate departureDate;
    private BigDecimal volume;
    private String sourceStation;
    private String destStation;
    private Integer flightsAmount;
    private BigDecimal tariff;
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

    public EmptyFlight(String departureDate, String volume, String sourceStation, String destStation, String flightsAmount, String tariff) {
        this.departureDate = LocalDate.parse(departureDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        this.volume = new BigDecimal(volume.replace(",", "."));
        this.sourceStation = sourceStation;
        this.destStation = destStation;
        this.flightsAmount =Integer.parseInt(flightsAmount);
        this.tariff = new BigDecimal(tariff.replace(",", ".").replace(" ", ""));
        this.type = "Порожний";
    }

    public EmptyFlight(BigDecimal volume, String sourceStation, String destStation, BigDecimal tariff, Integer flightsAmount) {
        this.volume = volume;
        this.sourceStation = sourceStation;
        this.destStation = destStation;
        this.tariff = tariff;
        this.flightsAmount = flightsAmount;
        this.type = "Порожний";
    }
}
