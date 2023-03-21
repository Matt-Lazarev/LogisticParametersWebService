package com.uraltrans.logisticparamservice.dto.station;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CargoResponse {
    private String success;
    private String errorText;
    private String idCargo;
    private String nameCargo;
}
