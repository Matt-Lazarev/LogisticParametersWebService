package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "cargos",
        uniqueConstraints = @UniqueConstraint(name = "unique_code_idx", columnNames = "code"))
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

    @Type(type="text")
    private String name;
}
