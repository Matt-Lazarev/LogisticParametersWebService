package com.uraltrans.logisticparamservice.dto.nodetailswagon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoDetailsWagon {
    private String success;
    private String errorText;
    private String departureDate;
    private String departureStation;
    private String destinationStation;
    private String departureStationName;
    private String destinationStationName;
    private String departureRoadName;
    private String destinationRoadName;
    private String cargoId;
    private String wagonType;
    private String volume;
    private String carNumber;
    private String p02;
    private String p06;
    private String p20;
    private String statusWagon;
    private String distanceFromCurrentStation;
    private String daysBeforeDatePlanRepair;
    private String restRun;
    private Boolean refurbished;
    private String idleDislocationStation;
}
