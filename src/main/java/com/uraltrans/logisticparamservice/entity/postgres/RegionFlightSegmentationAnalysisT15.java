package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="t15_analyzed")
public class RegionFlightSegmentationAnalysisT15 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="source_region")
    private String sourceRegion;

    @Column(name="desr_region")
    private String destRegion;

    private BigDecimal volume;

    private String type;

    @Column(name="rate_tariff")
    private BigDecimal rateTariff;

    @Column(name="flights_amount")
    private Integer flightsAmount;

    @Column(name="source_region_load_idle_days")
    private Integer sourceRegionLoadIdleDays;

    @Column(name="source_region_unload_idle_days")
    private Integer sourceRegionUnloadIdleDays;

    @Column(name="dest_region_load_idle_days")
    private Integer destRegionLoadIdleDays;

    @Column(name="dest_region_unload_idle_days")
    private Integer destRegionUnloadIdleDays;

    @Column(name="travel_days")
    private Integer travelDays;

    private BigDecimal profit;

    private Boolean isNextSegmentNofFound;

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", sourceRegion='" + sourceRegion + '\'' +
                ", destRegion='" + destRegion + '\'' +
                ", type='" + type + '\'' +
                ", volume='" + volume + '\'' +
                ", profit='" + profit + '\'' +
                ", isLast='" + isNextSegmentNofFound + '\'' +
                '}';
    }
}
