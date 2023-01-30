package com.uraltrans.logisticparamservice.service.mapper.mapstruct;

import com.uraltrans.logisticparamservice.dto.regionsegmentation.Flight;
import com.uraltrans.logisticparamservice.entity.postgres.RegionFlight;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RegionFlightMapper {

    RegionFlight mapToRegionFlight(Flight flight);

    default List<RegionFlight> toRegionFlightsList(List<Flight> flights){
        return flights.stream().map(this::mapToRegionFlight).collect(Collectors.toList());
    }
}
