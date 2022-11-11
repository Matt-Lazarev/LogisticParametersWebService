package com.uraltrans.logisticparamservice.dto.planfact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanFactRequest {
    @NotNull(message = "Поле 'id' отсутствует")
    private String id;

    @NotNull(message = "Поле 'departureStation' отсутствует")
    private String departureStation;

    private String destinationStation;

    @NotNull(message = "Поле 'wagonType' отсутствует")
    private String wagonType;

    @NotNull(message = "Поле 'volume' отсутствует")
    private String volume;
}
