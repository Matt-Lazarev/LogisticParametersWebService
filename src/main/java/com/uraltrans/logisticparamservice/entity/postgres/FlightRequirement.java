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
@Table(name = "flight_requirements")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlightRequirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="volume_from")
    private BigDecimal volumeFrom;

    @Column(name="volume_to")
    private BigDecimal volumeTo;

    @Column(name="source_station_code")
    private String sourceStationCode;

    @Column(name="destination_station_code")
    private String destinationStationCode;

    @Column(name="in_plan_orders")
    private Integer inPlanOrders;

    @Column(name="completed_orders")
    private Integer completedOrders;

    @Column(name="in_progress_orders")
    private Integer inProgressOrders;

    @Column(name="requirement_orders")
    private Integer requirementOrders;

    @Column(name="ut_rate")
    private BigDecimal utRate;
}
