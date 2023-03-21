package com.uraltrans.logisticparamservice.entity.utcsrs;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class UtcsrsFlightProfit {

    @Id
    private Integer id;
    private String sourceStationCode;
    private String destStationCode;
    private String cargoCode;
    private String cargo;
    private BigDecimal profit;
    private String currency;
    private BigDecimal volume;
    private Timestamp sendDate;
}
