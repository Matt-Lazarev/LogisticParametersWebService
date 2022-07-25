package com.uraltrans.logisticparamservice.entity.postgres;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "flight_profits")
@Accessors(chain = true)
@Getter
@Setter
public class FlightProfit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "send_date")
    private LocalDateTime sendDate;

    private BigDecimal volume;

    @Column(name = "source_station_code")
    private String sourceStationCode;

    @Column(name = "dest_station_code")
    private String destStationCode;

    @Column(name = "cargo_code")
    private String cargoCode;

    private String cargo;

    private BigDecimal profit;

    @JsonIgnore
    @Column(name = "currency_profit")
    private BigDecimal currencyProfit;

    @Column(name = "average_travel_time")
    private BigDecimal averageTravelTime;

    @Column(name = "flight_amount")
    private Integer flightAmount;

    @JsonIgnore
    private String currency;


    // Дата отправления -
    // Объем кузова -
    // Код Станция отправления +
    // Код Станция назначения +
    // Код груза                     --- код груза и наименовние делаются через JOIN
    // Наименование груза +

    // Доход за рейсы  с НДС
    // Количество  рейсов (всегда =1) +
    // Среднее Время груженного рейса
    // Валюта +
}
