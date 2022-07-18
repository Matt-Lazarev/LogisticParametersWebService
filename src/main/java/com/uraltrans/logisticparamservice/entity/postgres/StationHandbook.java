package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Table(name = "station_handbook")
@Accessors(chain = true)
@Getter
@Setter
public class StationHandbook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code5;

    private String code6;

    private String station;

    private String region; //ЖелезнодорожныеСтанции    ОтделениеДороги (Fld3371) -> ОтделенияЖелезныхДорог (Description)

    private String road; //ОтделенияЖелезныхДорог   Дорога (Fld3355) -> ЖелезныеДороги (Description)

    private String latitude;

    private String longitude;

}
