package com.uraltrans.logisticparamservice.service.mapper.mapstruct;

import com.uraltrans.logisticparamservice.dto.dislocation.DislocationResponse;
import com.uraltrans.logisticparamservice.entity.integration.projection.IntegrationDislocationProjection;
import com.uraltrans.logisticparamservice.entity.postgres.ActualFlight;
import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

import static com.uraltrans.logisticparamservice.utils.MappingUtils.fix1cDate;

@Mapper(componentModel = "spring")
public interface IntegrationDislocationMapper {
    ActualFlight toActualFlight(IntegrationDislocationProjection dislocationProjection);

    PotentialFlight toPotentialFlight(IntegrationDislocationProjection dislocationProjection);

    @Mapping(target = "cargoId", source = "actualFlight.cargoCode")
    @Mapping(target = "departureStation", source = "actualFlight.sourceStationCode")
    @Mapping(target = "destinationStation", source = "actualFlight.destinationStationCode")
    @Mapping(target = "dislocationStation", source = "actualFlight.dislocationStationCode")
    @Mapping(target = "flightType", source = "actualFlight.loaded")
    @Mapping(target = "featureWagon", source = "actualFlight.carState")
    @Mapping(target = "p02", source = "actualFlight.feature2")
    @Mapping(target = "p06", source = "actualFlight.feature6")
    @Mapping(target = "p09", source = "actualFlight.feature9")
    @Mapping(target = "p12", source = "actualFlight.feature12")
    @Mapping(target = "p20", source = "actualFlight.feature20")
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
