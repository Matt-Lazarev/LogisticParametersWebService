package com.uraltrans.logisticparamservice.service.mapper;

import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.utils.Mapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlightMapper {

    public List<Flight> mapRawFlightsDataToFlightsList(List<Map<String, Object>> flightsData) {
        return flightsData
                .stream()
                .map(this::mapToFlight)
                .collect(Collectors.toList());
    }

    private Flight mapToFlight(Map<String, Object> flightData) {
        return new Flight()
                .setAid((Integer) flightData.get("AID"))
                .setPrevFlightAid((Integer) flightData.get("PrevFlightID"))
                .setCarNumber((Integer) flightData.get("CarNumber"))
                .setInvNumber((String) flightData.get("InvNumber"))
                .setPrevInvNumber((String) flightData.get("PrevInvNumber"))
                .setNextInvNumber((String) flightData.get("NextInvNumber"))
                .setSendDate((Timestamp) flightData.get("SendDate"))
                .setSourceStation((String) flightData.get("SourceStation"))
                .setSourceStationCode((String) flightData.get("FVrh_3"))
                .setDestStation((String) flightData.get("DestStation"))
                .setDestStationCode((String) flightData.get("FVrh_4"))
                .setCargo((String) flightData.get("Cargo"))
                .setCargoCode6((String) flightData.get("CargoCode6"))
                .setCarType((String) flightData.get("CarType"))
                .setArriveToDestStationDate((Timestamp) flightData.get("DateInDate"))
                .setArriveToSourceStationDate((Timestamp) flightData.get("DateIn"))
                .setDepartureFromSourceStationDate((Timestamp) flightData.get("DateOut"))
                .setVolume((BigDecimal) flightData.get("Volume"))
                .setDistance(Mapper.toBigDecimal((Double) flightData.get("Distance")))
                .setLoaded((String) flightData.get("Loaded"))
                .setDepartureFromDestStationDate((Timestamp) flightData.get("Отправление со ст. назн."))
                .setUnloadOnDestStationDate((Timestamp) flightData.get("Выгрузка на ст. назн."))
                .setFlightKind((String) flightData.get("FlightKind"))
                .setNextFlightStartDate((Timestamp) flightData.get("Дата нач. след. Рейса (дата оформления вагона порожним)"))
                .setIsNotFirstEmpty((Boolean) flightData.get("IsNotFirstEmpty"))
                .setSourceContragent((String) flightData.get("SourceContragent"))
                .setClient((String) flightData.get("Klient"))
                .setSourceRailway((String) flightData.get("SourceRailway"))
                .setDestRailway((String) flightData.get("DestRailway"))
                .setTag2((String) flightData.get("Tag2"));
    }
}
