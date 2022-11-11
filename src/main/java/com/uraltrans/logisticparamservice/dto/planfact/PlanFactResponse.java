package com.uraltrans.logisticparamservice.dto.planfact;

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
public class PlanFactResponse {
    private String id;
    private String success;
    private String errorText;
    private String departureStation;
    private String destinationStation;
    private String volumeFrom;
    private String volumeTo;
    private Integer planQuantity;
    private Integer planReady;
    private Integer planInWork;
    private Integer shortage;
    private String rateFact;
}
