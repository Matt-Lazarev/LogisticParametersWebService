package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Accessors(chain = true)
@Getter
@Setter
@Entity
@Table(name = "station_handbook",
       uniqueConstraints = @UniqueConstraint(name = "unique_code6_idx", columnNames = "code6"))
public class StationHandbook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code5;

    private String code6;

    private String station;

    private String region;

    private String road;

    private String latitude;

    private String longitude;

}
