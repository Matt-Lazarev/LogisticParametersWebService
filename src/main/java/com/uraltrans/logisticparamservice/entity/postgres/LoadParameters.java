package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "load_parameters")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoadParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer daysToRetrieveData;
    private String nextDataLoadTime;
    private Integer maxLoadIdleDays;
    private Integer maxUnloadIdleDays;
    private Integer minLoadIdleDays;
    private Integer minUnloadIdleDays;
    private Double maxTravelTime;
    private Double minTravelTime;
    private Integer flightProfitDaysToRetrieveData;
    private String status;
    private String carType;
    private String managers;
    private String apikeyStationList;
    private String apikeyGeocoder;
    private Integer requestsAmountInDay;
    private String feature2;
    private String feature22;
    private Integer repairDaysCheck;
    private Boolean rateTariffState;
    private String token;
    private String sourceContragent;

    private String loadTimeT14;
    private String sourceDataT14;
    private String loadedMaskT14;
    private String unloadedMaskT14;
    private Integer daysThresholdT14;
}
