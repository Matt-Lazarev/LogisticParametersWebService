package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="region_flights")
public class RegionFlight {
    @Id
    private Integer id;

    @Column(name="source_station")
    private String sourceStation;

    @Column(name="dest_station")
    private String destStation;

    @Column(name="source_station_code")
    private String sourceStationCode;

    @Column(name="dest_station_code")
    private String destStationCode;

    @Column(name="source_region")
    private String sourceRegion;

    @Column(name="dest_region")
    private String destRegion;

    private BigDecimal volume;

    private String type;

    @Column(name="rate_tariff")
    private BigDecimal rateTariff;

    @Column(name="flights_amount")
    private Integer flightsAmount;

    @Column(name="travel_days")
    private Integer travelDays;

    @Column(name="source_station_load_idle_days")
    private Integer sourceStationLoadIdleDays;

    @Column(name="source_station_unload_idle_days")
    private Integer sourceStationUnloadIdleDays;

    @Column(name="dest_station_load_idle_days")
    private Integer destStationLoadIdleDays;

    @Column(name="dest_station_unload_idle_days")
    private Integer destStationUnloadIdleDays;
}
