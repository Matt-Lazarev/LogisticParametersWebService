package com.uraltrans.logisticparamservice.entity.postgres;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "potential_flights")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PotentialFlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="car_number")
    private String carNumber;

    private BigDecimal volume;

    @Column(name="dislocation_station_code")
    private String dislocationStationCode;

    @Column(name="source_station_code")
    private String sourceStationCode;

    @Column(name="destination_station_code")
    private String destinationStationCode;

    @Column(name="send_date")
    private LocalDateTime sendDate;

    private String feature9;

    @Column(name="car_state")
    private String carState;

    @Column(name="fleet_state")
    private String fleetState;

    @Column(name="current_order_begin")
    private LocalDateTime currentOrderBegin;

    @Column(name="current_order_end")
    private LocalDateTime currentOrderEnd;

    private String cargo;

    @Column(name = "cargo_code")
    private String cargoCode;

    @Column(name="plan_orders")
    private Integer planOrders;

    @Column(name="completed_orders")
    private Integer completedOrders;

    @Column(name="in_progress_orders")
    private Integer inProgressOrders;

    @Column(name="requirement_orders")
    private Integer requirementOrders;

    @JsonIgnore
    @Column(name="UT_rate")
    private BigDecimal utRate;

    @JsonIgnore
    private String feature2;

    @JsonIgnore
    private String feature6;

    @JsonIgnore
    private String feature12;

    @JsonIgnore
    private String feature20;

    @JsonIgnore
    private String loaded;

    @JsonIgnore
    @Column(name="wagon_type")
    private String wagonType;

    @JsonIgnore
    @Column(name="days_before_date_plan_repair")
    private BigDecimal daysBeforeDatePlanRepair;

    @JsonIgnore
    @Column(name="distance_from_current_station")
    private BigDecimal distanceFromCurrentStation;

    @JsonIgnore
    @Column(name="rest_run")
    private BigDecimal restRun;
}
