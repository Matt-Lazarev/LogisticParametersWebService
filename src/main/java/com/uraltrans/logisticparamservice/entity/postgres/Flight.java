package com.uraltrans.logisticparamservice.entity.postgres;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;


@Entity
@Table(name = "flights")
@Accessors(chain = true)
@Getter
@Setter
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aid")
    private Integer aid;

    @Column(name = "prev_flight_aid")
    private Integer prevFlightAid;

    @Column(name = "car_number")
    private Integer carNumber;

    @Column(name = "inv_number")
    private String invNumber;

    @Column(name = "prev_inv_number")
    private String prevInvNumber;

    @Column(name = "next_inv_number")
    private String nextInvNumber;

    @Column(name = "send_date")
    private Timestamp sendDate;

    @Column(name = "source_station")
    private String sourceStation;

    @Column(name = "source_station_code")
    private String sourceStationCode;

    @Column(name = "dest_station")
    private String destStation;

    @Column(name = "dest_station_code")
    private String destStationCode;

    private String cargo;

    @Column(name = "cargo_code6")
    private String cargoCode6;

    @Column(name = "car_type")
    private String carType;

    @Column(name = "arrive_to_dest_station_date")
    private Timestamp arriveToDestStationDate;

    @Column(name = "arrive_to_source_station_date")
    private Timestamp arriveToSourceStationDate;

    @Column(name = "departure_from_source_station_date")
    private Timestamp departureFromSourceStationDate;

    private BigDecimal volume;

    private BigDecimal distance;

    private BigDecimal travelTime;

    private String loaded;

    @Column(name = "departure_from_dest_station_date")
    private Timestamp departureFromDestStationDate;

    @Column(name = "unload_on_dest_station_date")
    private Timestamp unloadOnDestStationDate;

    @Column(name = "flight_kind")
    private String flightKind;

    @Column(name = "next_flight_start_date")
    private Timestamp nextFlightStartDate;

    @Column(name = "car_load_idle_days")
    private Integer carLoadIdleDays;

    @Column(name = "car_unload_idle_days")
    private Integer carUnloadIdleDays;

    private String comment;

    @Column(name = "is_not_first_empty")
    private Boolean isNotFirstEmpty;

    @Column(name = "source_railway")
    private String sourceRailway;

    @Column(name = "dest_railway")
    private String destRailway;

    @Column(name = "source_contragent")
    private String sourceContragent;

    private String client;

    private String tag2;

    private String tag22;

    @Override
    public String toString() {
        return "Flight{" +
                "aid=" + aid +
                ", sourceStationCode='" + sourceStationCode + '\'' +
                ", destStationCode='" + destStationCode + '\'' +
                ", loading=" + carLoadIdleDays +
                ", unloading=" + carUnloadIdleDays +
                ", distance=" + (distance != null ? String.format("%.2f", distance.doubleValue()) : null) +
                ", travelTime=" + (travelTime != null ? String.format("%.2f", travelTime.doubleValue()) : null) +
                '}';
    }
}

