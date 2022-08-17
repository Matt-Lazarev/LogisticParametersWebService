package com.uraltrans.logisticparamservice.dto.addressing;

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
public class AddressingResponse {
    private String departureStation;
    private String destinationStation;
    private String cargoId;
    private String wagonType;
    private String volume;
    private Integer planQuantity;
    private String carNumber;
    private String destinationStationCurrentFlight;
    private String dislocationStationCurrentFlight;
    private String tariff;
    private String rate;
    private String rateFact;
}
