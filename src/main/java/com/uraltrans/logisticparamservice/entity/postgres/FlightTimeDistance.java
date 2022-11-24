package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "flight_times_distances")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class FlightTimeDistance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "departure_station_code")
    private String departureStationCode;

    @Column(name = "destination_station_code")
    private String destinationStationCode;

    @Column(name = "type_flight")
    private String typeFlight;

    private String distance;

    @Column(name = "travel_time")
    private String travelTime;

}
