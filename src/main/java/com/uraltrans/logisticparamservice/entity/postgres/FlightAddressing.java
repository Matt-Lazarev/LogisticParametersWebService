package com.uraltrans.logisticparamservice.entity.postgres;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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

    @Column(name = "tariff_id")
    private Integer tariffId;

    @Column(name = "rate_id")
    private Integer rateId;

    @Column(name = "car_number")
    private String carNumber;

    private BigDecimal volume;

    @Column(name = "source_station")
    private String sourceStation;

    @Column(name = "source_station_code")
    private String sourceStationCode;

    @Column(name = "source_station_region")
    private String sourceStationRegion;

    @Column(name = "source_station_road")
    private String sourceStationRoad;

    @Column(name = "destination_station")
    private String destinationStation;

    @Column(name = "destination_station_code")
    private String destinationStationCode;

    @Column(name = "destination_station_region")
    private String destinationStationRegion;

    @Column(name = "destination_station_road")
    private String destinationStationRoad;

    @Column(name = "current_flight_dest_station")
    private String currentFlightDestStation;

    @Column(name = "current_flight_dest_station_code")
    private String currentFlightDestStationCode;

    @Column(name = "current_flight_dest_station_region")
    private String currentFlightDestStationRegion;

    @Column(name = "current_flight_dest_station_road")
    private String currentFlightDestStationRoad;

    @Column(name = "dislocation_station")
    private String dislocationStation;

    @Column(name = "dislocation_station_code")
    private String dislocationStationCode;

    @Column(name = "cargo")
    private String cargo;

    @Column(name = "cargo_code")
    private String cargoCode;

    @Column(name = "client_orders_cargo")
    private String clientOrderCargo;

    @Column(name = "client_orders_cargo_code")
    private String clientOrderCargoCode;

    @Column(name = "plan_orders")
    private Integer planOrders;

    @Column(name = "requirement_orders")
    private Integer requirementOrders;

    private BigDecimal tariff;

    private BigDecimal rate;

    @Column(name = "ut_rate")
    private BigDecimal utRate;

    @JsonIgnore
    private String loaded;

    @JsonIgnore
    @Column(name = "wagon_type")
    private String wagonType;

    @JsonIgnore
    @Column(name = "date_from")
    private String dateFrom;

    @JsonIgnore
    @Column(name = "date_to")
    private String dateTo;

    @JsonIgnore
    private String feature2;

    @JsonIgnore
    private String feature12;

    @JsonIgnore
    private String feature20;

    @JsonIgnore
    private String clientNextTask;

    @JsonIgnore
    @Column(name = "car_state")
    private String carState;

    @JsonIgnore
    @Column(name = "days_before_date_plan_repair")
    private BigDecimal daysBeforeDatePlanRepair;

    @JsonIgnore
    @Column(name = "distance_from_current_station")
    private BigDecimal distanceFromCurrentStation;

    @JsonIgnore
    @Column(name = "rest_run")
    private BigDecimal restRun;

    @Column(name = "thickness_wheel")
    private BigDecimal thicknessWheel;

    @Column(name = "thickness_comb")
    private BigDecimal thicknessComb;

    @Column(name = "nonworking_park")
    private Boolean nonworkingPark;

    private Boolean refurbished;

    private Boolean rejected;

    public FlightAddressing(FlightAddressing addressing) {
        this.carNumber = addressing.carNumber;
        this.volume = addressing.volume;

        this.sourceStationCode = addressing.sourceStationCode;
        this.sourceStation = addressing.sourceStation;
        this.sourceStationRegion = addressing.sourceStationRegion;
        this.sourceStationRoad = addressing.sourceStationRoad;

        this.destinationStationCode = addressing.destinationStationCode;
        this.destinationStation = addressing.destinationStation;
        this.destinationStationRegion = addressing.destinationStationRegion;
        this.destinationStationRoad = addressing.destinationStationRoad;

        this.currentFlightDestStationCode = addressing.currentFlightDestStationCode;
        this.currentFlightDestStation = addressing.currentFlightDestStation;
        this.currentFlightDestStationRegion = addressing.currentFlightDestStationRegion;
        this.currentFlightDestStationRoad = addressing.currentFlightDestStationRoad;

        this.dislocationStation = addressing.dislocationStation;
        this.dislocationStationCode = addressing.dislocationStationCode;

        this.cargoCode = addressing.cargoCode;
        this.planOrders = addressing.planOrders;
        this.requirementOrders = addressing.requirementOrders;
        this.loaded = addressing.loaded;
        this.wagonType = addressing.wagonType;
        this.utRate = addressing.utRate;

        this.feature2 = addressing.feature2;
        this.feature12 = addressing.feature12;
        this.feature20 = addressing.feature20;
        this.clientNextTask = addressing.clientNextTask;
        this.carState = addressing.carState;

        this.daysBeforeDatePlanRepair = addressing.daysBeforeDatePlanRepair;
        this.distanceFromCurrentStation = addressing.distanceFromCurrentStation;
        this.restRun = addressing.restRun;

        this.thicknessWheel = addressing.thicknessWheel;
        this.thicknessComb = addressing.thicknessComb;
        this.nonworkingPark = addressing.nonworkingPark;
        this.refurbished = addressing.refurbished;
        this.rejected = addressing.rejected;
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
