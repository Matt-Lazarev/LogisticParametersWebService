package com.uraltrans.logisticparamservice.dto.regionsegmentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SegmentationDto {
    private String sourceRegion;
    private String destRegion;
    private BigDecimal volume;
    private String segmentType;
    private String path;
    private Integer wholeDays;
    private Integer wholeTravelDays;
    private Integer wholeLoadDays;
    private Integer wholeUnloadDays;
    private BigDecimal wholeProfit;
    private List<Segment> segments;
}
