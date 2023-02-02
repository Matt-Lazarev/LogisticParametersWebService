package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "t15_result")
public class SegmentationResultT15 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private BigDecimal volume;

    @Column(name="segment_type")
    private String segmentType;

    @Column(name = "source_region")
    private String sourceRegion;

    @Column(name = "dest_region")
    private String destRegion;

    @Column(name = "path", length = 1000)
    private String path;

    @Column(name = "first_segment")
    private String firstSegment;

    @Column(name = "second_segment")
    private String secondSegment;

    @Column(name = "third_segment")
    private String thirdSegment;

    @Column(name = "first_profit")
    private BigDecimal firstProfit;

    @Column(name = "second_profit")
    private BigDecimal secondProfit;

    @Column(name = "third_profit")
    private BigDecimal thirdProfit;

    @Column(name = "total_profit")
    private BigDecimal totalProfit;

    @Column(name = "total_days")
    private Integer totalDays;

    @Column(name = "total_travel_days")
    private Integer totalTravelDays;

    @Column(name = "total_load_days")
    private Integer totalLoadDays;

    @Column(name = "total_unload_days")
    private Integer totalUnloadDays;

    @Column(name = "first_market")
    private Integer firstMarket;

    @Column(name = "first_utk")
    private Integer firstUtk;

    @Column(name = "first_empty_market")
    private Integer firstEmptyMarket;

    @Column(name = "first_empty_utk")
    private Integer firstEmptyUtk;
}
