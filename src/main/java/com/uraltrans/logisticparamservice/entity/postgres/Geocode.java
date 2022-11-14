package com.uraltrans.logisticparamservice.entity.postgres;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "geocodes",
       uniqueConstraints = @UniqueConstraint(name = "unique_station_cod_idx", columnNames = "station_code"))
@Getter
@Setter
@NoArgsConstructor
public class Geocode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="station_code")
    private String stationCode;

    private String latitude;

    private String longitude;

    @Column(name="expired_at")
    private LocalDateTime expiredAt;

    public Geocode(String stationCode, String latitude, String longitude) {
        this.stationCode = stationCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.expiredAt = LocalDateTime.now().plusDays(30);
    }
}
