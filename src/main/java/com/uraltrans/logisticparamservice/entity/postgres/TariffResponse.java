package com.uraltrans.logisticparamservice.entity.postgres;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tariff_responses")
public class TariffResponse {
    @Id
    private String id;
    private String uid;
    private Integer distance;
    private Integer travelTime;
    private Integer loadingUnloading;
}

