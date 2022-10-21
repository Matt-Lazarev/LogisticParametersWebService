package com.uraltrans.logisticparamservice.dto.carinfo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder @ToString
public class CarRepairDto {
    private String carNumber;
    private Boolean nonworkingPark;
    private Boolean refurbished;
    private Boolean rejected;
}
