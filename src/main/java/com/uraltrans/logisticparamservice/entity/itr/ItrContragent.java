package com.uraltrans.logisticparamservice.entity.itr;

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
public class ItrContragent {
    @Id
    private Integer id;
    private String name;
    private String inn;
    private String email;
    private String company;
    private String type;
}
