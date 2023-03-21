package com.uraltrans.logisticparamservice.entity.itr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ItrClientOrder {
    @Id
    private Integer id;
    private String carNumber;
    private String documentStatus;
    private String carType;
    private Double volumeFrom;
    private Double volumeTo;
    private String client;
    private String sourceStationCode6;
    private String destinationCode6;
    private String cargo;
    private String cargoCode;
    private BigDecimal carsAmount;
    private String manager;
    private Timestamp fromDate;
    private Timestamp toDate;
    private BigDecimal utRate;
}
