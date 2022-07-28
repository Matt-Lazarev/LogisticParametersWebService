package com.uraltrans.logisticparamservice.entity.postgres;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "client_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    @Column(name = "car_type")
    private String carType;

    @Column(name = "volume_from")
    private BigDecimal volumeFrom;

    @Column(name = "volume_to")
    private BigDecimal volumeTo;

    private String client;

    @Column(name = "source_station_code6")
    private String sourceStationCode6;

    @Column(name = "destination_station_code6")
    private String destinationStationCode6;

    private String cargo;

    @Column(name = "cargo_code")
    private String cargoCode;

    @Column(name = "cars_amount")
    private Integer carsAmount;

    private String manager;

    @Column(name = "from_date")
    private LocalDateTime fromDate;

    @Column(name = "to_date")
    private LocalDateTime toDate;

    @JsonIgnore
    private String firm;
}
