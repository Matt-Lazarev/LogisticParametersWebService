package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "flight_idles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightIdle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="departure_station")
    private String departureStation;

    @Column(name="departure_station_code")
    private String departureStationCode;

    private String cargo;

    private String cargoCode;

    @Column(name = "wagon_type")
    private String wagonType;

    private String volume;

    private Integer loading;

    private Integer unloading;

    public FlightIdle(String departureStation, String departureStationCode,
                      String cargoCode, String cargo,
                      String wagonType, BigDecimal volume,
                      Integer loading, Integer unloading) {
        this.departureStation = departureStation;
        this.departureStationCode = departureStationCode;
        this.cargo = cargo;
        this.cargoCode = cargoCode;
        this.wagonType = wagonType;
        this.volume = volume != null ? volume.toString() : null;
        this.loading = loading;
        this.unloading = unloading;
    }
}
