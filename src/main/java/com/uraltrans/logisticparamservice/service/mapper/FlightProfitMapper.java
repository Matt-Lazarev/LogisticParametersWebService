package com.uraltrans.logisticparamservice.service.mapper;

import com.uraltrans.logisticparamservice.entity.postgres.FlightProfit;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlightProfitMapper {
    private static final int SHIFT_1C_YEARS = 2000;

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
                .setCurrencyProfit((BigDecimal) data.get("_TotalCurrencyProfit"))
                .setCurrency((String) data.get("_Currency"))
                .setCargo((String) data.get("_Cargo"))
                .setCargoCode((String) data.get("_CargoCode"))
                .setVolume((BigDecimal) data.get("_Volume"))
                .setSendDate(fix1CDate((Timestamp) data.get("_SendDate")))
                .setFlightAmount(1);
    }

    private LocalDateTime fix1CDate(Timestamp timestamp){
        LocalDateTime date = timestamp.toLocalDateTime();
        return date.getYear() == SHIFT_1C_YEARS ? null : date.minusYears(SHIFT_1C_YEARS);
    }
}
