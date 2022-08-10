package com.uraltrans.logisticparamservice.entity.postgres;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "actual_flights")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualFlight {

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

    @Column(name="completed_orders")
    private Integer completedOrders;

    @Column(name="in_progress_orders")
    private Integer inProgressOrders;

    @JsonIgnore
    private String feature2;

    @JsonIgnore
    private String feature12;

    @JsonIgnore
    private String loaded;
}
