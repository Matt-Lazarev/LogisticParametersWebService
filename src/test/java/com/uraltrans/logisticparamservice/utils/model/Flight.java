package com.uraltrans.logisticparamservice.utils.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Flight {

    private Integer carNumber;
    private String invNumber;
    private String sourceStation;
    private String destStation;
    private LocalDate arriveDate;
    private Double idle;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return Objects.equals(carNumber, flight.carNumber) && Objects.equals(invNumber, flight.invNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carNumber, invNumber);
    }
}
