package com.uraltrans.logisticparamservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoadDataRequestDto {
    private Integer daysToRetrieveData;
    private Integer maxLoadIdleDays;
    private Integer maxUnloadIdleDays;
    private Integer minLoadIdleDays;
    private Integer minUnloadIdleDays;
}
