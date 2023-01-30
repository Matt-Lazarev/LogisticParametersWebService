package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Entity
@Table(name="t7")
public class ProfitThresholdT7 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="from_date")
    private LocalDate fromDate;

    @Column(name="to_date")
    private LocalDate toDate;

    @Column(name="from_volume")
    private Integer fromVolume;

    @Column(name="to_volume")
    private Integer toVolume;

    private Integer cost;
}
