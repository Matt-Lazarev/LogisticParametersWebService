package com.uraltrans.logisticparamservice.service.mapper;

import com.uraltrans.logisticparamservice.entity.postgres.ActualFlight;
import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;
import com.uraltrans.logisticparamservice.utils.Mapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActualFlightMapper {

    public List<ActualFlight> mapRawDataToActualFlightsList(List<Map<String, Object>> rawActualFlightsData){
        return rawActualFlightsData
                .stream()
                .map(this::mapToActualFlight)
                .collect(Collectors.toList());
    }

    public List<PotentialFlight> mapRawDataToPotentialFlightsList(List<Map<String, Object>> rawActualFlightsData){
        return rawActualFlightsData
                .stream()
                .map(this::mapToPotentialFlight)
                .collect(Collectors.toList());
    }

    private ActualFlight mapToActualFlight(Map<String, Object> data) {
        return ActualFlight.builder()
                .carNumber((String) data.get("CarNumber"))
                .volume((BigDecimal) data.get("Volume"))
                .dislocationStationCode((String) data.get("DislocationStationCode"))
                .sourceStationCode((String) data.get("SourceStationCode"))
                .destinationStationCode((String) data.get("DestinationStationCode"))
                .sendDate(Mapper.fix1cDate((Timestamp) data.get("SendDate")))
                .feature2((String) data.get("Feature2"))
                .feature12((String) data.get("Feature12"))
                .carState((String) data.get("CarState"))
                .fleetState((String) data.get("FleetState"))
                .feature9((String) data.get("Feature9"))
                .currentOrderBegin(Mapper.fix1cDate((Timestamp) data.get("BeginOrderDate")))
                .currentOrderEnd(Mapper.fix1cDate((Timestamp) data.get("EndOrderDate")))
                .build();
    }

    private PotentialFlight mapToPotentialFlight(Map<String, Object> data) {
        return PotentialFlight.builder()
                .carNumber((String) data.get("CarNumber"))
                .volume((BigDecimal) data.get("Volume"))
                .dislocationStationCode((String) data.get("DislocationStationCode"))
                .sourceStationCode((String) data.get("SourceStationCode"))
                .destinationStationCode((String) data.get("DestinationStationCode"))
                .sendDate(Mapper.fix1cDate((Timestamp) data.get("SendDate")))
                .feature2((String) data.get("Feature2"))
                .feature6((String) data.get("Feature6"))
                .feature12((String) data.get("Feature12"))
                .carState((String) data.get("CarState"))
                .fleetState((String) data.get("FleetState"))
                .feature9((String) data.get("Feature9"))
                .loaded((String) data.get("Loaded"))
                .currentOrderBegin(Mapper.fix1cDate((Timestamp) data.get("BeginOrderDate")))
                .currentOrderEnd(Mapper.fix1cDate((Timestamp) data.get("EndOrderDate")))
                .build();
    }
}
