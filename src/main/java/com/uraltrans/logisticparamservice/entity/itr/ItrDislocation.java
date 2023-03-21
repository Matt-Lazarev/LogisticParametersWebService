package com.uraltrans.logisticparamservice.entity.itr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ItrDislocation {
    @Id
    private Integer id;
    private String carNumber;
    private String volume;
    private String dislocationStation;
    private String departureStation;
    private String departureStationName;
    private String departureRoadName;
    private String destinationStation;
    private String destinationStationName;
    private String destinationRoadName;
    private String getDepartureDate;
    private String getP02;
    private String getP06;
    private String getP09;
    private String getP12;
    private String getP20;
    private String getStatusWagon;
    private String wagonType;
    private String flightType;
    private String cargoId;
    private String daysBeforeDatePlanRepair;
    private String distanceFromCurrentStation;
    private String restRun;
    private String idleDislocationStation;
}
