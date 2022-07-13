package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "flight_times_distances")
@Getter
@Setter
@NoArgsConstructor
public class FlightTimeDistance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "departure_station_code")
    private String departureStationCode;

    @Column(name = "destination_station_code")
    private String destinationStationCode;

    @Column(name = "flight_type")
    private String flightType;

    private String distance;

    @Column(name = "travel_time")
    private String travelTime;

}
