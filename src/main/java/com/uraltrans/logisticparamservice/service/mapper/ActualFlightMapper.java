package com.uraltrans.logisticparamservice.service.mapper;

import com.uraltrans.logisticparamservice.dto.dislocation.DislocationResponse;
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

    public List<DislocationResponse> mapToResponses(List<ActualFlight> actualFlights){
        return actualFlights
                .stream()
                .map(this::mapToResponse)
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
                .feature6((String) data.get("Feature6"))
                .feature9((String) data.get("Feature9"))
                .feature12((String) data.get("Feature12"))
                .feature20((String) data.get("Feature20"))//FIXME
                .cargo((String) data.get("Cargo"))
                .cargoCode((String) data.get("CargoCode"))
                .carState((String) data.get("CarState"))
                .fleetState((String) data.get("FleetState"))
                .loaded((String) data.get("Loaded"))
                .wagonType((String) data.get("WagonType"))
                .operation((String) data.get("Operation"))
                .owner((String) data.get("Owner"))
                .operationDateTime(null)//FIXME
                .carModel((String) data.get("Model"))
                .nextManager(null)//FIXME
                .nextStation((String) data.get("NextStation"))
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
                .cargo((String) data.get("Cargo"))
                .cargoCode((String) data.get("CargoCode"))
                .carState((String) data.get("CarState"))
                .fleetState((String) data.get("FleetState"))
                .feature9((String) data.get("Feature9"))
                .loaded((String) data.get("Loaded"))
                .wagonType((String) data.get("WagonType"))
                .currentOrderBegin(Mapper.fix1cDate((Timestamp) data.get("BeginOrderDate")))
                .currentOrderEnd(Mapper.fix1cDate((Timestamp) data.get("EndOrderDate")))
                .build();
    }

    private DislocationResponse mapToResponse(ActualFlight actualFlight) {
        return DislocationResponse.builder()
                .departureStation(actualFlight.getSourceStationCode())
                .destinationStation(actualFlight.getDestinationStationCode())
                .dislocationStation(actualFlight.getDislocationStationCode())
                .dislocationStation(actualFlight.getDislocationStationCode())
                .cargoId(actualFlight.getCargoCode())
                .wagonType("Крытый")
                .volume(String.valueOf(actualFlight.getVolume()))
                .carNumber(actualFlight.getCarNumber())
                .owner(actualFlight.getOwner())
                .operation(actualFlight.getOperation())
                .operationDateTime("") //FIXME
                .flightType(actualFlight.getLoaded())
                .featureWagon(actualFlight.getCarModel())
                .p02(actualFlight.getFeature2())
                .p06(actualFlight.getFeature6())
                .p09(actualFlight.getFeature9())
                .p12(actualFlight.getFeature12())
                .p20("")//FIXME
                .nextManager("")//FIXME
                .nextStation(actualFlight.getNextStation())
                .statusWagon(actualFlight.getCarState())
                .build();
    }
}
