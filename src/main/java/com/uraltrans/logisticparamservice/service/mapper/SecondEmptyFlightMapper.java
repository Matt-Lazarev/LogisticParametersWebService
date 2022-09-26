package com.uraltrans.logisticparamservice.service.mapper;

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
                .currEmptyFlightRegistrationDate(Mapper.toLocalDateTime((Timestamp) data.get("Дата прибытия вагона на станцию  назначения (порожним)")))
                .currEmptyFlightArriveAtDestStationDate(Mapper.toLocalDateTime((Timestamp) data.get("Дата нач. след. Рейса (дата оформления вагона порожним)")))
                .AID((Integer) data.get("AID"))
                .prevFlightId((Integer) data.get("PrevFlightID"))
                .isNotFirstEmpty((Boolean) data.get("IsNotFirstEmpty"))
                .loaded((String) data.get("Loaded"))
                .build();
    }
}
