package com.uraltrans.logisticparamservice.service.mapper.mapstruct;

import com.uraltrans.logisticparamservice.dto.secondempty.SecondEmptyFlightResponse;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.entity.postgres.SecondEmptyFlight;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SecondEmptyFlightMapper {
    @Mapping(target = "cargoCode", source = "flight.cargoCode6")
    @Mapping(target = "AID", source = "flight.aid")
    @Mapping(target = "departureFromSourceStation", source = "flight.departureFromSourceStationDate")
    @Mapping(target = "arriveToDestStation", source = "flight.arriveToDestStationDate")
    @Mapping(target = "currEmptyFlightRegistrationDate", source = "flight.nextFlightStartDate")
    @Mapping(target = "currEmptyFlightArriveAtDestStationDate", source = "flight.arriveToDestStationDate")
    SecondEmptyFlight toSecondEmptyFlight(Flight flight);

    List<SecondEmptyFlight> toSecondEmptyFlightList(List<Flight> flights);

    @Mapping(target = "id", expression = "java(String.valueOf(flight.getId()))")
    @Mapping(target = "aid", expression = "java(String.valueOf(flight.getAID()))")
    @Mapping(target = "carNumber", expression = "java(String.valueOf(flight.getCarNumber()))")
    @Mapping(target = "resultDay", expression = "java(String.valueOf(flight.getIdleDays() != null && flight.getIdleDays().doubleValue() > 0.0))")
    SecondEmptyFlightResponse toSecondEmptyFlightResponse(SecondEmptyFlight flight);

    List<SecondEmptyFlightResponse> toSecondEmptyFlightResponseList(List<SecondEmptyFlight> flights);
}
