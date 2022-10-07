package com.uraltrans.logisticparamservice.entity.postgres;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "second_empty_flights")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecondEmptyFlight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_contragent")
    private String sourceContragent;

    private String client;

    private BigDecimal volume;

    @Column(name = "car_type")
    private String carType;

    @Column(name = "car_number")
    private Integer carNumber;

    @Column(name = "source_railway")
    private String sourceRailway;

    @Column(name = "source_station")
    private String sourceStation;

    @Column(name = "dest_railway")
    private String destRailway;

    @Column(name = "dest_station")
    private String destStation;

    @Column(name = "prev_empty_flight_arrive_at_dest_station_date")
    private LocalDateTime prevEmptyFlightArriveAtDestStationDate;

    @Column(name = "curr_empty_flight_arrive_at_dest_station_date")
    private LocalDateTime currEmptyFlightArriveAtDestStationDate;

    @Column(name = "prev_empty_flight_registration_date")
    private LocalDateTime prevEmptyFlightRegistrationDate;

    @Column(name = "curr_empty_flight_registration_date")
    private LocalDateTime currEmptyFlightRegistrationDate;

    private BigDecimal idleDays;

    private Integer AID;

    @Column(name = "prev_flight_id")
    private Integer prevFlightId;

    @Column(name = "is_not_first_empty")
    private Boolean isNotFirstEmpty;

    private String loaded;

    @Column(name="cargo_code")
    private String cargoCode;
}
