package com.uraltrans.logisticparamservice.service.mapper;

import com.uraltrans.logisticparamservice.entity.postgres.FlightProfit;
import com.uraltrans.logisticparamservice.entity.postgres.FlightRequirement;
import com.uraltrans.logisticparamservice.utils.Mapper;
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

    private FlightRequirement mapToFlightRequirement(Map<String, Object> data) {
        FlightRequirement f = FlightRequirement.builder()
                .volumeTo((BigDecimal) data.get("volume_to"))
                .volumeFrom((BigDecimal) data.get("volume_from"))
                .sourceStationCode((String) data.get("source_station_code6"))
                .destinationStationCode((String) data.get("destination_station_code6"))
                .inPlanOrders(Mapper.toInteger((BigInteger) data.get("cars_amount")))
               // .inPlanOrders((Integer) data.get("cars_amount"))
                .completedOrders(Mapper.toInteger((BigDecimal) data.get("completed_orders")))
                .inProgressOrders(Mapper.toInteger((BigDecimal) data.get("in_progress_orders")))
                .build();

        f.setRequirementOrders(f.getInPlanOrders() - f.getCompletedOrders() - f.getInProgressOrders());
        return f;
    }
}
