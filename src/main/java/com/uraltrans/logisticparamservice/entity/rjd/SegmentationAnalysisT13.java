package com.uraltrans.logisticparamservice.entity.rjd;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "t13")
public class SegmentationAnalysisT13 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long version;

    @Column(name = "whole_go_days")
    private Integer wholeGoDays;

    private String region;

    @Column(name = "first_profit")
    private BigDecimal firstProfit;

    @Column(name = "first_segment", length = 4000)
    private String firstSegment;

    @Column(name = "second_segment", length = 4000)
    private String secondSegment;

    @Column(name = "path", length = 4000)
    private String path;

    @Column(name = "whole_profit")
    private BigDecimal wholeProfit;

    @Column(name = "second_profit")
    private BigDecimal secondProfit;

    @Column(name = "date_from")
    private LocalDateTime dateFrom;

    @Column(name = "whole_days")
    private Integer wholeDays;

    @Column(name = "whole_load_days")
    private Integer wholeLoadDays;

    private Integer volume;

    @Column(name = "first_amount")
    private Integer firstAmount;

    @Column(name = "second_amount")
    private Integer secondAmount;

    @Column(name = "whole_unload_days")
    private Integer wholeUnloadDays;

    private String type;

    @Column(name = "from_station")
    private String fromStation;

    @Column(name = "from_station_name")
    private String fromStationName;

    @Column(name = "date_to")
    private LocalDateTime dateTo;

    @Column(name = "third_profit")
    private BigDecimal thirdProfit;

    @Column(name = "third_amount")
    private Integer thirdAmount;

    @Column(name = "third_good")
    private String thirdGood;

    @Column(name = "first_good")
    private String firstGood;

    @Column(name = "second_good")
    private String secondGood;

    @Column(name = "third_segment", length = 4000)
    private String thirdSegment;

    @Column(name = "seconds1load_unload")
    private BigDecimal seconds1loadUnload;

    @Column(name = "first_segment_code")
    private String firstSegmentCode;

    private BigDecimal seconds1days;

    @Column(name = "second_segment_code")
    private String secondSegmentCode;

    private BigDecimal firsts1profit;

    private BigDecimal firsts1rate;

    private BigDecimal firsts1days;

    private BigDecimal seconds1rate;

    @Column(name = "firsts1load_unload")
    private BigDecimal firsts1load_unload;

    private BigDecimal seconds1profit;

    @Column(name = "code_path", length = 4000)
    private String codePath;

    @Column(name = "third_segment_code")
    private String thirdSegmentCode;

    @Column(name = "first_days_empty_go")
    private Integer firstDaysEmptyGo;

    @Column(name = "third_days_empty_go")
    private Integer thirdDaysEmptyGo;

    @Column(name = "first_days_full_go")
    private Integer firstDaysFullGo;

    @Column(name = "second_load_unload")
    private Integer secondLoadUnload;

    @Column(name = "first_load_unload")
    private Integer firstLoadUnload;

    @Column(name = "third_load_unload")
    private Integer thirdLoadUnload;

    @Column(name = "second_days_full_go")
    private Integer secondDaysFullGo;

    @Column(name = "second_days_empty_go")
    private Integer secondDaysEmptyGo;

    @Column(name = "third_days_full_go")
    private Integer thirdDaysFullGo;

    @Column(name = "third_empty_price")
    private BigDecimal thirdEmptyPrice;

    @Column(name = "first_empty_price")
    private BigDecimal firstEmptyPrice;

    @Column(name = "second_empty_price")
    private BigDecimal secondEmptyPrice;
}
