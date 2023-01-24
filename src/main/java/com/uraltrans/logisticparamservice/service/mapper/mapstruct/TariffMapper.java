package com.uraltrans.logisticparamservice.service.mapper.mapstruct;

import com.uraltrans.logisticparamservice.dto.ratetariff.TariffRequest;
import com.uraltrans.logisticparamservice.dto.ratetariff.TariffResultResponse;
import com.uraltrans.logisticparamservice.entity.postgres.TariffResponse;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TariffMapper {
    String CARGO_MOCK = "601009";
    String WAGON_TYPE_MOCK = "КР";

    TariffResponse toTariffResponse(TariffResultResponse.Detail detail);

    default List<TariffResponse> mapToTariffResponsesList(TariffResultResponse tariffResultResponse){
        String uid = tariffResultResponse.getUid();
        return tariffResultResponse.getDetails()
                .stream()
                .map(this::toTariffResponse)
                .peek(tr -> tr.setUid(uid))
                .collect(Collectors.toList());
    }

    default List<TariffRequest> mapToTariffRequests(List<String[]> flights){
        AtomicInteger id = new AtomicInteger(1);
        return flights
                .stream()
                .map(f -> toTariffRequest(id.getAndIncrement(), f))
                .collect(Collectors.toList());
    }

    default TariffRequest toTariffRequest(int id, String[] params) {
        return TariffRequest.builder()
                .id(String.valueOf(id))
                .departureStation(params[0])
                .destinationStation(params[1])
                .volune(params[2])
                .flightType(params[3])
                .cargo(CARGO_MOCK)
                .wagonType(WAGON_TYPE_MOCK)
                .build();
    }
}
