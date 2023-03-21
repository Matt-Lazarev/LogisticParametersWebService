package com.uraltrans.logisticparamservice.service.mapper.mapstruct;

import com.uraltrans.logisticparamservice.entity.itr.ItrFlight;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItrFlightMapper {
    Flight toFlight(ItrFlight itrFlight);

    default List<Flight> toFlightList(List<ItrFlight> itrFlights){
        return itrFlights.stream().map(this::toFlight).toList();
    }
}
