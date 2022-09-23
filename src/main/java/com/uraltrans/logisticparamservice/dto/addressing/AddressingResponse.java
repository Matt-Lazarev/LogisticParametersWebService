package com.uraltrans.logisticparamservice.dto.addressing;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressingResponse {
    private String departureStation;
    private String destinationStation;
    private String cargoId;
    private String wagonType;
    private String volume;
    private Integer planQuantity;
    private Integer shortage;
    private String carNumber;
    private String destinationStationCurrentFlight;
    private String dislocationStationCurrentFlight;
    private String tariff;
    private String rate;
    private String rateFact;
    private String p2;
    private String p12;
    private String p20;
    private String statusWagon;
    private BigDecimal daysBeforeDatePlanRepair;
    private BigDecimal distanceFromCurrentStation;
    private BigDecimal restRun;
    private BigDecimal thicknessWheel;
    private BigDecimal thicknessComb;
    private Boolean nonworkingPark;
    private Boolean refurbished;
    private Boolean rejected;
}
