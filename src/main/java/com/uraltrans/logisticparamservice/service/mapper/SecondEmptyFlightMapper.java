package com.uraltrans.logisticparamservice.service.mapper;

import com.uraltrans.logisticparamservice.dto.secondempty.SecondEmptyFlightResponse;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.entity.postgres.SecondEmptyFlight;
import com.uraltrans.logisticparamservice.utils.Mapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecondEmptyFlightMapper {
    public List<SecondEmptyFlight> mapToSecondEmptyFlight(List<Flight> flights) {
        return flights
                .stream()
                .map(this::toSecondEmptyFlightTest)
                .collect(Collectors.toList());
    }

    private SecondEmptyFlight toSecondEmptyFlightTest(Flight flight) {
        return SecondEmptyFlight.builder()
                .volume(flight.getVolume())
                .carType(flight.getCarType())
                .carNumber(flight.getCarNumber())
                .sourceStation(flight.getSourceStation())
                .destStation(flight.getDestStation())
                .currEmptyFlightRegistrationDate(Mapper.toLocalDateTime(flight.getNextFlightStartDate()))
                .currEmptyFlightArriveAtDestStationDate(Mapper.toLocalDateTime(flight.getArriveToDestStationDate()))
                .AID(flight.getAid())
                .invNumber(flight.getInvNumber())
                .nextInvNumber(flight.getNextInvNumber())
                .prevFlightId(flight.getPrevFlightAid())
                .isNotFirstEmpty(flight.getIsNotFirstEmpty())
                .loaded(flight.getLoaded())
                .sourceRailway(flight.getSourceRailway())
                .destRailway(flight.getDestRailway())
                .sourceContragent(flight.getSourceContragent())
                .client(flight.getClient())
                .cargoCode(flight.getCargoCode6())
                .tag2(flight.getTag2())
                .tag22(flight.getTag22())
                .sourceStationCode(flight.getSourceStationCode())
                .destStationCode(flight.getDestStationCode())
                .departureFromSourceStation(flight.getDepartureFromSourceStationDate())
                .arriveToDestStation(flight.getArriveToDestStationDate())
                .build();
    }

    public List<SecondEmptyFlightResponse> mapToSecondEmptyFlightResponses(List<SecondEmptyFlight> flights) {
        return flights
                .stream()
                .map(this::toSecondEmptyFlightResponse)
                .collect(Collectors.toList());
    }

    private SecondEmptyFlightResponse toSecondEmptyFlightResponse(SecondEmptyFlight flight) {
        return SecondEmptyFlightResponse.builder()
                .id(String.valueOf(flight.getId()))
                .aid(String.valueOf(flight.getAID()))
                .docNumber(flight.getInvNumber())
                .carNumber(String.valueOf(flight.getCarNumber()))
                .build();
    }
}
