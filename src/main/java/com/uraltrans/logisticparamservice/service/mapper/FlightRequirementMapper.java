package com.uraltrans.logisticparamservice.service.mapper;

import com.uraltrans.logisticparamservice.dto.planfact.PlanFactResponse;
import com.uraltrans.logisticparamservice.entity.postgres.FlightRequirement;
import com.uraltrans.logisticparamservice.utils.MappingUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlightRequirementMapper {
    public List<FlightRequirement> mapToList(List<Map<String, Object>> rawFlightList){
        return rawFlightList
                .stream()
                .map(this::mapToFlightRequirement)
                .collect(Collectors.toList());
    }

    public List<PlanFactResponse> mapToResponses(List<FlightRequirement> requirements, String requestId){
        return requirements
                .stream()
                .map(this::mapToPlanFactResponse)
                .peek(f -> f.setId(requestId))
                .collect(Collectors.toList());
    }

    private FlightRequirement mapToFlightRequirement(Map<String, Object> data) {
        FlightRequirement f = FlightRequirement.builder()
                .volumeTo((BigDecimal) data.get("volume_to"))
                .volumeFrom((BigDecimal) data.get("volume_from"))
                .sourceStationCode((String) data.get("source_station_code6"))
                .destinationStationCode((String) data.get("destination_station_code6"))
                .inPlanOrders(MappingUtils.toInteger((BigInteger) data.get("cars_amount")))
                .completedOrders(MappingUtils.toInteger((BigDecimal) data.get("completed_orders")))
                .inProgressOrders(MappingUtils.toInteger((BigDecimal) data.get("in_progress_orders")))
                .utRate((BigDecimal) data.get("ut_rate"))
                .build();

        f.setRequirementOrders(f.getInPlanOrders() - f.getCompletedOrders() - f.getInProgressOrders());
        return f;
    }

    private PlanFactResponse mapToPlanFactResponse(FlightRequirement requirement) {
        return PlanFactResponse.builder()
                .success("true")
                .errorText("")
                .departureStation(requirement.getSourceStationCode())
                .destinationStation(requirement.getDestinationStationCode())
                .volumeFrom(String.valueOf(requirement.getVolumeFrom()))
                .volumeTo(String.valueOf(requirement.getVolumeTo()))
                .planQuantity(requirement.getInPlanOrders())
                .planReady(requirement.getCompletedOrders())
                .planInWork(requirement.getInProgressOrders())
                .shortage(requirement.getRequirementOrders())
                .rateFact(String.valueOf(requirement.getUtRate()))
                .build();
    }
}
