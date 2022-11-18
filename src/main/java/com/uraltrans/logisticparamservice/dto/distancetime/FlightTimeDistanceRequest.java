package com.uraltrans.logisticparamservice.dto.distancetime;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class FlightTimeDistanceRequest {
    @NotNull(message="Не указано поле 'uid'")
    private String uid;

    @Valid
    @NotNull(message="Не указано поле 'details'")
    private List<Detail> details;

    @Getter
    @Setter
    public static class Detail{
        @NotNull(message="Не указано поле 'id' в массиве 'details'")
        private String id;

        @NotNull(message="Не указано поле 'departureStation' в массиве 'details'")
        private String departureStation;

        @NotNull(message="Не указано поле 'destinationStation' в массиве 'details'")
        private String destinationStation;

        @NotNull(message="Не указано поле 'typeFlight' в массиве 'details'")
        private String typeFlight;
    }
}
