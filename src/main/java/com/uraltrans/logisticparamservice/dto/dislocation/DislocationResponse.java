package com.uraltrans.logisticparamservice.dto.dislocation;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DislocationResponse {
    private String departureStation;
    private String destinationStation;
    private String cargoId;
    private String wagonType;
    private String volume;
    private String carNumber;
    private String dislocationStation;
    private String owner;
    private String operation;
    private String operationDateTime;
    private String flightType;
    private String featureWagon;
    private String p02;
    private String p06;
    private String p09;
    private String p12;
    private String p20;
    private String nextManager;
    private String nextStation;
    private String statusWagon;
    private BigDecimal daysBeforeDatePlanRepair;
    private BigDecimal distanceFromCurrentStation;
    private BigDecimal restRun;
}
