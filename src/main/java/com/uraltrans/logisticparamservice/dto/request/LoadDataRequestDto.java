package com.uraltrans.logisticparamservice.dto.request;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
public class LoadDataRequestDto {
    private Integer daysToRetrieveData;
    private String nextDataLoadTime;
    private Integer maxLoadIdleDays;
    private Integer maxUnloadIdleDays;
    private Integer minLoadIdleDays;
    private Integer minUnloadIdleDays;
    private Double maxTravelTime;
    private Double minTravelTime;
}
