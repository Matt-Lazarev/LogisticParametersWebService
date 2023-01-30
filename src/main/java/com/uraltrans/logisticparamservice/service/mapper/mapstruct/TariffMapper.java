package com.uraltrans.logisticparamservice.service.mapper.mapstruct;

import com.uraltrans.logisticparamservice.dto.ratetariff.TariffRequest;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TariffMapper {
    String CARGO_MOCK = "601009";
    String WAGON_TYPE_MOCK = "КР";

    default List<TariffRequest> mapToTariffRequests(List<String[]> flights){
        return flights
                .stream()
                .map(this::toTariffRequest)
                .collect(Collectors.toList());
    }

    default TariffRequest toTariffRequest(String[] params) {
        return TariffRequest.builder()
                .id(params[0])
                .departureStation(params[1])
                .destinationStation(params[2])
                .volune(params[3])
                .flightType(params[4])
                .cargo(CARGO_MOCK)
                .wagonType(WAGON_TYPE_MOCK)
                .build();
    }
}
