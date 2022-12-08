package com.uraltrans.logisticparamservice.service.mapper;

import com.uraltrans.logisticparamservice.entity.postgres.NoDetailsWagon;
import com.uraltrans.logisticparamservice.utils.Mapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NoDetailsWagonMapper {
    public List<NoDetailsWagon> mapToNoDetailsWagons(List<Map<String, Object>> dislocations) {
        return dislocations
                .stream()
                .map(this::mapToNoDetailsWagon)
                .collect(Collectors.toList());
    }

    private NoDetailsWagon mapToNoDetailsWagon(Map<String, Object> data) {
        return NoDetailsWagon.builder()
                .departureDate(String.valueOf(Mapper.fix1cDate((Timestamp) data.get("SendDate"))))
                .departureStation((String) data.get("SourceStationCode"))
                .destinationStation((String) data.get("DestinationStationCode"))
                .departureStationName((String) data.get("SourceStation"))
                .destinationStationName((String) data.get("DestinationStation"))
                .departureRoadName((String) data.get("SourceStationRoad"))
                .destinationRoadName((String) data.get("DestinationStationRoad"))
                .cargoId((String) data.get("CargoCode"))
                .wagonType((String) data.get("WagonType"))
                .volume(String.valueOf(data.get("Volume")))
                .carNumber((String) data.get("CarNumber"))
                .p02((String) data.get("Feature2"))
                .p06((String) data.get("Feature6"))
                .p20((String) data.get("Feature20"))
                .statusWagon((String) data.get("CarState"))
                .daysBeforeDatePlanRepair(String.valueOf(data.get("DaysBeforeDatePlanRepair")))
                .distanceFromCurrentStation(String.valueOf(data.get("DistanceFromCurrentStation")))
                .restRun(String.valueOf(data.get("RestRun")))
                .idleDislocationStation((String) data.get("IdleDislocationStation"))
                .build();
    }
}
