package com.uraltrans.logisticparamservice.dto.freewagon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FreeWagonResponse {
    private String success;
    private String errorText;
    private String departureStation;
    private String destinationStation;
    private String cargoId;
    private String wagonType;
    private String volume;
    private String carNumber;
    private String destinationStationCurrentFlight;
    private String dislocationStationCurrentFlight;
    private String p02;
    private String p12;
    private String p20;
    private String clientNextTask;
    private String statusWagon;
    private String distanceFromCurrentStation;
    private String daysBeforeDatePlanRepair;
    private String restRun;
    private Boolean refurbished;
    private String thicknessWheel;
    private String thicknessComb;
}
