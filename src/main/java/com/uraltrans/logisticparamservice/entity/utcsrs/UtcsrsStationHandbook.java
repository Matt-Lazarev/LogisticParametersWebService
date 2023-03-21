package com.uraltrans.logisticparamservice.entity.utcsrs;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class UtcsrsStationHandbook {
    @Id
    private Integer id;

    private String code;
    private String description;
    private String region;
    private String road;
    private String latitude;
    private String longitude;
    private String excludeFromSecondEmptyFlight;
    private String lock;
}
