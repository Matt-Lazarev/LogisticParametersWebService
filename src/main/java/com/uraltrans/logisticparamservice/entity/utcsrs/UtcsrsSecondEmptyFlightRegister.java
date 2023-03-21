package com.uraltrans.logisticparamservice.entity.utcsrs;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class UtcsrsSecondEmptyFlightRegister {
    @Id
    private Integer id;
    private String sourceStation;
    private String destStation;
}
