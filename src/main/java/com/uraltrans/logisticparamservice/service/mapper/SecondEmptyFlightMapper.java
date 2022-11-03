package com.uraltrans.logisticparamservice.service.mapper;

import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.entity.postgres.SecondEmptyFlight;
import com.uraltrans.logisticparamservice.utils.Mapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
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
}
