package com.uraltrans.logisticparamservice.entity.integration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class IntegrationCarRepair {
    @Id
    private Integer id;
    private String carNumber;
    private String nonworkingPark;
    private String refurbished;
    private String rejected;
    private String requiresRepair;
    private String thicknessWheel;
    private String thicknessComb;
}
