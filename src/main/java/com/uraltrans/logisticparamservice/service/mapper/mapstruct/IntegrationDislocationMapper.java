package com.uraltrans.logisticparamservice.service.mapper.mapstruct;

import com.uraltrans.logisticparamservice.dto.dislocation.DislocationResponse;
import com.uraltrans.logisticparamservice.entity.integration.projection.IntegrationDislocationProjection;
import com.uraltrans.logisticparamservice.entity.postgres.ActualFlight;
import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import static com.uraltrans.logisticparamservice.utils.MappingUtils.fix1cDate;

@Mapper(componentModel = "spring")
public interface IntegrationDislocationMapper {
    ActualFlight toActualFlight(IntegrationDislocationProjection dislocationProjection);

    PotentialFlight toPotentialFlight(IntegrationDislocationProjection dislocationProjection);

    DislocationResponse toDislocationResponse(ActualFlight actualFlight);

    default List<ActualFlight> toActualFlightList(List<IntegrationDislocationProjection> dislocations){
        return dislocations.stream()
                .map(this::toActualFlight)
                .peek(f -> f.setSendDate(fix1cDate(f.getSendDate())))
                .peek(f -> f.setOperationDateTime(fix1cDate(f.getOperationDateTime())))
                .peek(f -> f.setCurrentOrderBegin(fix1cDate(f.getCurrentOrderBegin())))
                .peek(f -> f.setCurrentOrderEnd(fix1cDate(f.getCurrentOrderEnd())))
                .toList();
    }

    default List<PotentialFlight> toPotentialFlightList(List<IntegrationDislocationProjection> dislocations){
        return dislocations.stream()
                .map(this::toPotentialFlight)
                .peek(f -> f.setSendDate(fix1cDate(f.getSendDate())))
                .peek(f -> f.setCurrentOrderBegin(fix1cDate(f.getCurrentOrderBegin())))
                .peek(f -> f.setCurrentOrderEnd(fix1cDate(f.getCurrentOrderEnd())))
                .toList();
    }

    default List<DislocationResponse> toDislocationResponseList(List<ActualFlight> actualFlights, String requestId){
        return actualFlights
                .stream()
                .map(this::toDislocationResponse)
                .peek(f -> f.setId(requestId))
                .peek(f -> f.setWagonType("Крытый"))
                .peek(f -> f.setSuccess("true"))
                .peek(f -> f.setErrorText(""))
                .collect(Collectors.toList());
    }
}
