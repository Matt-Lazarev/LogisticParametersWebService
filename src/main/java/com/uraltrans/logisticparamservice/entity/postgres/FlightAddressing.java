package com.uraltrans.logisticparamservice.entity.postgres;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name="source_station")
    private String sourceStation;

    @Column(name="source_station_code")
    private String sourceStationCode;

    @Column(name = "source_station_region")
    private String sourceStationRegion;

    @Column(name = "source_station_road")
    private String sourceStationRoad;

    @Column(name="destination_station")
    private String destinationStation;

    @Column(name="destination_station_code")
    private String destinationStationCode;

    @Column(name = "destination_station_region")
    private String destinationStationRegion;

    @Column(name = "destination_station_road")
    private String destinationStationRoad;

    @Column(name="current_flight_dest_station")
    private String currentFlightDestStation;

    @Column(name="current_flight_dest_station_code")
    private String currentFlightDestStationCode;

    @Column(name="current_flight_dest_station_region")
    private String currentFlightDestStationRegion;

    @Column(name="current_flight_dest_station_road")
    private String currentFlightDestStationRoad;

    @Column(name="cargo")
    private String cargo;

    @Column(name="cargo_code")
    private String cargoCode;

    @Column(name="client_orders_cargo")
    private String clientOrderCargo;

    @Column(name="client_orders_cargo_code")
    private String clientOrderCargoCode;

    @Column(name="requirement_orders")
    private Integer requirementOrders;

    private BigDecimal tariff;

    private BigDecimal rate;

    @JsonIgnore
    private String loaded;

    @JsonIgnore
    @Column(name="wagon_type")
    private String wagonType;

    @JsonIgnore
    @Column(name="date_from")
    private String dateFrom;

    @JsonIgnore
    @Column(name="date_to")
    private String dateTo;

    public FlightAddressing(FlightAddressing addressing) {
        this.carNumber = addressing.carNumber;
        this.volume = addressing.volume;
        this.sourceStationCode = addressing.sourceStationCode;
        this.destinationStationCode = addressing.destinationStationCode;
        this.currentFlightDestStationCode = addressing.currentFlightDestStationCode;
        this.cargoCode = addressing.cargoCode;
        this.requirementOrders = addressing.requirementOrders;
        this.loaded = addressing.loaded;
        this.wagonType = addressing.wagonType;
    }

    @Override
    public String toString() {
        return "FlightAddressing{" +
                "carNumber='" + carNumber + '\'' +
                ", volume=" + volume +
                ", sourceStationCode='" + sourceStationCode + '\'' +
                ", destinationStationCode='" + destinationStationCode + '\'' +
                ", currentFlightDestStationCode='" + currentFlightDestStationCode + '\'' +
                ", cargoCode='" + cargoCode + '\'' +
                ", requirementOrders=" + requirementOrders +
                ", loaded='" + loaded + '\'' +
                ", wagonType='" + wagonType + '\'' +
                '}';
    }
}
