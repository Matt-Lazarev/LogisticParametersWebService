package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "load_parameters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

    public LoadParameters(Integer daysToRetrieveData, String nextDataLoadTime,
                          Integer maxLoadIdleDays, Integer maxUnloadIdleDays,
                          Integer minLoadIdleDays, Integer minUnloadIdleDays,
                          Double maxTravelTime, Double minTravelTime,
                          Integer flightProfitDaysToRetrieveData, String status,
                          String carType, String managers, String apikeyGeocoder,
                          String apikeyStationList, Integer requestsAmountInDay,
                          String feature2) {
        this.daysToRetrieveData = daysToRetrieveData;
        this.nextDataLoadTime = nextDataLoadTime;
        this.maxLoadIdleDays = maxLoadIdleDays;
        this.maxUnloadIdleDays = maxUnloadIdleDays;
        this.minLoadIdleDays = minLoadIdleDays;
        this.minUnloadIdleDays = minUnloadIdleDays;
        this.maxTravelTime = maxTravelTime;
        this.minTravelTime = minTravelTime;
        this.flightProfitDaysToRetrieveData = flightProfitDaysToRetrieveData;
        this.status = status;
        this.carType = carType;
        this.managers = managers;
        this.apikeyGeocoder = apikeyGeocoder;
        this.apikeyStationList = apikeyStationList;
        this.requestsAmountInDay = requestsAmountInDay;
        this.feature2 = feature2;
    }
}
