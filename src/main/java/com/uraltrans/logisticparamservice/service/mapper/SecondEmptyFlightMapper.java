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

    public List<SecondEmptyFlight> mapRawDataToList(List<Map<String, Object>> data){
        return data.stream()
                .map(this::toSecondEmptyFlight)
                .collect(Collectors.toList());
    }

    private SecondEmptyFlight toSecondEmptyFlight(Map<String, Object> data){
        return SecondEmptyFlight.builder()
                .sourceContragent((String) data.get("SourceContragent"))
                .client((String) data.get("Klient"))
                .volume((BigDecimal) data.get("Volume"))
                .carType((String) data.get("CarType"))
                .carNumber((Integer) data.get("CarNumber"))
                .sourceRailway((String) data.get("SourceRailway"))
                .sourceStation((String) data.get("SourceStation"))
                .destRailway((String) data.get("DestRailway"))
                .destStation((String) data.get("DestStation"))
                .currEmptyFlightRegistrationDate(Mapper.toLocalDateTime((Timestamp) data.get("Дата нач. след. Рейса (дата оформления вагона порожним)")))
                .currEmptyFlightArriveAtDestStationDate(Mapper.toLocalDateTime((Timestamp) data.get("DateInDate")))
                .AID((Integer) data.get("AID"))
                .prevFlightId((Integer) data.get("PrevFlightID"))
                .isNotFirstEmpty((Boolean) data.get("IsNotFirstEmpty"))
                .loaded((String) data.get("Loaded"))
                .build();
    }

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
                .sourceStationCode(flight.getSourceStationCode())
                .destStationCode(flight.getDestStationCode())
                .build();
    }
}
