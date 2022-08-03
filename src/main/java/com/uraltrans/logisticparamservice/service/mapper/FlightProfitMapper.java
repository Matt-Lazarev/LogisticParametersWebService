package com.uraltrans.logisticparamservice.service.mapper;

import com.uraltrans.logisticparamservice.entity.postgres.FlightProfit;
import com.uraltrans.logisticparamservice.utils.Mapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlightProfitMapper {
    public List<FlightProfit> mapToList(List<Map<String, Object>> flightsData){
        return flightsData
                .stream()
                .map(this::mapToFlightProfit)
                .collect(Collectors.toList());
    }

    private FlightProfit mapToFlightProfit(Map<String, Object> data) {
        return new FlightProfit()
                .setSourceStationCode((String) data.get("_SourceStationCode"))
                .setDestStationCode((String) data.get("_DestStationCode"))
                .setProfit((BigDecimal) data.get("_TotalProfit"))
                .setCurrency((String) data.get("_Currency"))
                .setCargo((String) data.get("_Cargo"))
                .setCargoCode((String) data.get("_CargoCode"))
                .setVolume((BigDecimal) data.get("_Volume"))
                .setSendDate(Mapper.fix1cDate((Timestamp) data.get("_SendDate")))
                .setFlightAmount(1);
    }
}
