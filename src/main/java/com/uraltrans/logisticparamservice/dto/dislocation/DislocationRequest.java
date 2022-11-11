package com.uraltrans.logisticparamservice.dto.dislocation;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DislocationRequest {
    @NotNull(message = "Поле 'id' отсутствует")
    private String id;

    @NotNull(message = "Поле 'destinationStationCurrentFlight' отсутствует")
    private String destinationStationCurrentFlight;

    @NotNull(message = "Поле 'wagonType' отсутствует")
    private String wagonType;

    @NotNull(message = "Поле 'volume' отсутствует")
    private String volume;
}
