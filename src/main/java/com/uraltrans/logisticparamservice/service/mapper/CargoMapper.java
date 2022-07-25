package com.uraltrans.logisticparamservice.service.mapper;

import com.uraltrans.logisticparamservice.dto.station.CargoResponse;
import com.uraltrans.logisticparamservice.entity.postgres.Cargo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CargoMapper {

    public List<Cargo> mapRawDataToCargoList(List<Map<String, Object>> rawCargoData){
        return rawCargoData
                .stream()
                .map(this::mapToCargo)
                .collect(Collectors.toList());
    }

    public List<CargoResponse> mapToCargoResponseList(List<Cargo> cargos) {
        return cargos
                .stream()
                .map(this::mapToCargoResponse)
                .collect(Collectors.toList());
    }

    private Cargo mapToCargo(Map<String, Object> data) {
        return Cargo.builder()
                .code((String) data.get("_Code"))
                .name((String) data.get("_CargoName"))
                .build();
    }

    private CargoResponse mapToCargoResponse(Cargo cargo) {
        return CargoResponse.builder()
                .success("true")
                .errorText("")
                .idCargo(cargo.getCode())
                .nameCargo(cargo.getName())
                .build();
    }
}
