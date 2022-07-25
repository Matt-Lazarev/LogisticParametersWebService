package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "cargos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;
}
