package com.uraltrans.logisticparamservice.dto.regionsegmentation;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class Flight {
    private LocalDate departureDate;
    private BigDecimal volume;
    private String sourceStation;
    private String destStation;
    private BigDecimal rateTariff;
    private Integer flightsAmount;
    private String sourceRegion;
    private String destRegion;
    private String sourceStationCode;
    private String destStationCode;
    private String type;
    private Integer travelDays;
    private Integer sourceStationLoadIdleDays;
    private Integer sourceStationUnloadIdleDays;
    private Integer destStationLoadIdleDays;
    private Integer destStationUnloadIdleDays;

    public Flight(String departureDate, String volume, String sourceStation, String destStation,
                  String rateTariff, String flightsAmount, String type) {
        this.departureDate = LocalDate.parse(departureDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        this.volume = new BigDecimal(volume.replace(",", "."));
        this.sourceStation = sourceStation;
        this.destStation = destStation;
        this.rateTariff = new BigDecimal(rateTariff.replace(",", ".").replace(" ", ""));
        this.flightsAmount = Integer.parseInt(flightsAmount);
        this.type = type;
    }

    public Flight(BigDecimal volume, String sourceStation, String destStation,
                  String type, BigDecimal rateTariff, Integer flightsAmount) {
        this.volume = volume;
        this.sourceStation = sourceStation;
        this.destStation = destStation;
        this.type = type;
        this.rateTariff = rateTariff;
        this.flightsAmount = flightsAmount;
    }
}
