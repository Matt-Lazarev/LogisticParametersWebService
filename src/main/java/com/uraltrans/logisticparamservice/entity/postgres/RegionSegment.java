package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "t15_region_segments")
public class RegionSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="loaded_source_region")
    private String loadedSourceRegion;

    @Column(name="loaded_dest_region")
    private String loadedDestRegion;

    @Column(name="empty_source_region")
    private String emptySourceRegion;

    @Column(name="empty_dest_region")
    private String emptyDestRegion;

    private String path;

    private BigDecimal profit;

    @Column(name="segment_travel_days")
    private Integer segmentTravelDays;

    @Column(name="load_idle_days")
    private Integer loadIdleDays;

    @Column(name="unload_idle_days")
    private Integer unloadIdleDays;

    public RegionSegment(String loadedSourceRegion, String loadedDestRegion,
                         String emptySourceRegion, String emptyDestRegion,
                         BigDecimal profit, Integer segmentTravelDays,
                         Integer loadIdleDays, Integer unloadIdleDays) {
        this.loadedSourceRegion = loadedSourceRegion;
        this.loadedDestRegion = loadedDestRegion;
        this.emptySourceRegion = emptySourceRegion;
        this.emptyDestRegion = emptyDestRegion;
        this.path = loadedSourceRegion + "->" + loadedDestRegion + ";" + emptySourceRegion + "->" + emptyDestRegion + ";";
        this.profit = profit;
        this.segmentTravelDays = segmentTravelDays;
        this.loadIdleDays = loadIdleDays;
        this.unloadIdleDays = unloadIdleDays;
    }

    @Override
    public String toString() {
        return "RegionSegment{" +
                "path='" + path + '\'' +
                ", profit=" + profit +
                '}';
    }
}
