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
                .feature20((String) data.get("Feature20"))
                .cargo((String) data.get("Cargo"))
                .cargoCode((String) data.get("CargoCode"))
                .carState((String) data.get("CarState"))
                .fleetState((String) data.get("FleetState"))
                .loaded((String) data.get("Loaded"))
                .wagonType((String) data.get("WagonType"))
                .operation((String) data.get("Operation"))
                .owner((String) data.get("Owner"))
                .operationDateTime(Mapper.fix1cDate((Timestamp) data.get("OperationDateTime")))
                .carModel((String) data.get("Model"))
                .nextManager((String) data.get("ManagerNextTask"))
                .nextStation((String) data.get("NextStation"))
                .daysBeforeDatePlanRepair((BigDecimal) data.get("DaysBeforeDatePlanRepair"))
                .distanceFromCurrentStation((BigDecimal) data.get("DistanceFromCurrentStation"))
                .restRun((BigDecimal) data.get("RestRun"))
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
                .feature9((String) data.get("Feature9"))
                .feature12((String) data.get("Feature12"))
                .feature20((String) data.get("Feature20"))
                .clientNextTask((String) data.get("ClientNextTask"))
                .cargo((String) data.get("Cargo"))
                .cargoCode((String) data.get("CargoCode"))
                .carState((String) data.get("CarState"))
                .fleetState((String) data.get("FleetState"))
                .loaded((String) data.get("Loaded"))
                .wagonType((String) data.get("WagonType"))
                .currentOrderBegin(Mapper.fix1cDate((Timestamp) data.get("BeginOrderDate")))
                .currentOrderEnd(Mapper.fix1cDate((Timestamp) data.get("EndOrderDate")))
                .daysBeforeDatePlanRepair((BigDecimal) data.get("DaysBeforeDatePlanRepair"))
                .distanceFromCurrentStation((BigDecimal) data.get("DistanceFromCurrentStation"))
                .restRun((BigDecimal) data.get("RestRun"))
                .build();
    }

    private DislocationResponse mapToResponse(ActualFlight actualFlight) {
        return DislocationResponse.builder()
                .success("true")
                .errorText("")
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
                .operationDateTime(Mapper.toString(actualFlight.getOperationDateTime()))
                .flightType(actualFlight.getLoaded())
                .featureWagon(actualFlight.getCarModel())
                .p02(actualFlight.getFeature2())
                .p06(actualFlight.getFeature6())
                .p09(actualFlight.getFeature9())
                .p12(actualFlight.getFeature12())
                .p20(actualFlight.getFeature20())
                .nextManager(actualFlight.getNextManager())
                .nextStation(actualFlight.getNextStation())
                .statusWagon(actualFlight.getCarState())
                .distanceFromCurrentStation(actualFlight.getDistanceFromCurrentStation())
                .daysBeforeDatePlanRepair(actualFlight.getDaysBeforeDatePlanRepair())
                .restRun(actualFlight.getRestRun())
                .build();
    }
}
