package com.uraltrans.logisticparamservice.service.mapper.mapstruct;

import com.uraltrans.logisticparamservice.dto.station.CargoResponse;
import com.uraltrans.logisticparamservice.entity.postgres.Cargo;
import com.uraltrans.logisticparamservice.entity.utcsrs.projection.UtcsrsCargoProjection;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UtcsrsCargoMapper {
    Cargo toCargo(UtcsrsCargoProjection utcsrsCargo);

    List<Cargo> toCargoList(List<UtcsrsCargoProjection> utcsrsCargos);

    @Mapping(target = "idCargo", source = "cargo.code")
    @Mapping(target = "nameCargo", source = "cargo.name")
    CargoResponse toCargoResponse(Cargo cargo);

    List<CargoResponse> toCargoResponseList(List<Cargo> cargos);

//    @AfterMapping
//    default void cargoPostConstruct(@MappingTarget List<CargoResponse> cargoResponses) {
//        cargoResponses.forEach(cr -> {
//            cr.setSuccess("true");
//            cr.setErrorText("");
//        });
//    }

    @AfterMapping
    default void cargoPostConstruct(@MappingTarget CargoResponse cargoResponse) {
        cargoResponse.setSuccess("true");
        cargoResponse.setErrorText("");
        System.out.println("after");
    }
}
