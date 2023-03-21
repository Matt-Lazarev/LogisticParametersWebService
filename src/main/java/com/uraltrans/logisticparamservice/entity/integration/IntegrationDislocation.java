package com.uraltrans.logisticparamservice.entity.integration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class IntegrationDislocation {
    @Id
    private Integer id;
    private String carNumber;
    private BigDecimal volume;
    private String dislocationStationCode;
    private String sourceStationCode;
    private String sourceStation;
    private String sourceStationRoad;
    private String destinationStationCode;
    private String destinationStation;
    private String destinationStationRoad;
    private Timestamp sendDate;
    private String feature2;
    private String feature6;
    private String feature9;
    private String feature12;
    private String feature20;
    private String clientNextTask;
    private String managerNextTask;
    private Timestamp operationDateTime;
    private String carState;
    private String fleetState;
    private Timestamp currentOrderBegin;
    private Timestamp currentOrderEnd;
    private String loaded;
    private String wagonType;
    private String cargo;
    private String cargoCode;
    private String owner;
    private String operation;
    private String model;
    private String nextStation;
    private BigDecimal daysBeforeDatePlanRepair;
    private BigDecimal distanceFromCurrentStation;
    private BigDecimal restRun;
    private String idleDislocationStation;
}
