package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "flight_idles")
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class FlightIdle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="departure_station")
    private String departureStation;

    private String cargo;

    @Column(name = "wagon_type")
    private String wagonType;

    private String volume;

    private Integer loading;

    private Integer unloading;

    public FlightIdle(String departureStation, String code, String cargo,
                      String wagonType, BigDecimal volume,
                      Integer loading, Integer unloading) {
        this.departureStation = departureStation + " (" + code + ")";
        this.cargo = cargo;
        this.wagonType = wagonType;
        this.volume = volume != null ? volume.toString() : null;
        this.loading = loading;
        this.unloading = unloading;
    }
}
