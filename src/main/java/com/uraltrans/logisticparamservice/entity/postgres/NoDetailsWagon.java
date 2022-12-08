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

@Entity
@Table(name = "no_details_wagons")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoDetailsWagon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    @Column(name="departure_date")
    private String departureDate;

    @Column(name="departure_station")
    private String departureStation;

    @Column(name="destination_station")
    private String destinationStation;

    @Column(name="departure_station_name")
    private String departureStationName;

    @Column(name="destination_station_name")
    private String destinationStationName;

    @Column(name="departure_road_name")
    private String departureRoadName;

    @Column(name = "destination_road_name")
    private String destinationRoadName;

    @Column(name="cargo_id")
    private String cargoId;

    @Column(name="wagon_type")
    private String wagonType;

    private String volume;

    @Column(name="car_number")
    private String carNumber;

    private String p02;

    private String p06;

    private String p20;

    @Column(name="status_wagon")
    private String statusWagon;

    @Column(name="distance_from_current_station")
    private String distanceFromCurrentStation;

    @Column(name = "days_before_date_plan_repair")
    private String daysBeforeDatePlanRepair;

    @Column(name="rest_run")
    private String restRun;

    private Boolean refurbished;

    @Column(name="idle_dislocation_station")
    private String idleDislocationStation;
}
