package com.uraltrans.logisticparamservice.dto.secondempty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SecondEmptyFlightResponse {
    private String id;
    private String carNumber;
    private String docNumber;
    private String aid;
}
