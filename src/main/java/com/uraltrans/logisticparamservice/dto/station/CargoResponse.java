package com.uraltrans.logisticparamservice.dto.station;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CargoResponse {
    private String success;
    private String errorText;
    private String idCargo;
    private String nameCargo;
}
