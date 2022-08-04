package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "flight_addressings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightAddressing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="car_number")
    private String carNumber;

    private BigDecimal volume;

    @Column(name="source_station_code")
    private String sourceStationCode;

    @Column(name="destination_station_code")
    private String destinationStationCode;

    @Column(name="current_flight_dest_station_code")
    private String currentFlightDestStationCode;

    @Column(name="cargo_code")
    private String cargoCode;

    @Column(name="requirement_orders")
    private Integer requirementOrders;

    private Integer tariff;

    private Integer rate;
}
