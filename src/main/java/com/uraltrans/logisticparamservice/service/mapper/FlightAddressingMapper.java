package com.uraltrans.logisticparamservice.service.mapper;

import com.uraltrans.logisticparamservice.dto.ratetariff.RateRequest;
import com.uraltrans.logisticparamservice.dto.ratetariff.TariffRequest;
import com.uraltrans.logisticparamservice.entity.postgres.FlightAddressing;
import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlightAddressingMapper {
    private static final String TARIFF_CALLBACK_URL = "http://10.168.1.6:8080/calc/tariff";
    private static final String RATE_CALLBACK_URL = "http://10.168.1.6:8080/calc/rate";

    public List<FlightAddressing> mapRawDataToFlightAddressingList(
            List<Map<String, Object>> data, PotentialFlight potentialFlight){
        return data
                .stream()
                .map(this::toFlightAddressing)
                .peek(f -> f.setCurrentFlightDestStationCode(potentialFlight.getDestinationStationCode()))
                .peek(f -> f.setCarNumber(potentialFlight.getCarNumber()))
                .peek(f -> f.setCargoCode(potentialFlight.getCargoCode()))
                .peek(f -> f.setVolume(potentialFlight.getVolume()))
                .peek(f -> f.setLoaded(potentialFlight.getLoaded()))
                .peek(f -> f.setWagonType(potentialFlight.getWagonType()))
                .collect(Collectors.toList());
    }

    public List<TariffRequest> mapToTariffRequests(List<FlightAddressing> flightAddressings){
        return flightAddressings
                .stream()
                .map(this::toTariffRequest)
                .collect(Collectors.toList());
    }

    public List<RateRequest> mapToRateRequests(List<FlightAddressing> flightAddressings){
        return flightAddressings
                .stream()
                .map(this::toRateRequest)
                .collect(Collectors.toList());
    }

    private FlightAddressing toFlightAddressing(Map<String, Object> data) {
        return FlightAddressing.builder()
                .carNumber((String) data.get("car_number"))
                .volume((BigDecimal) data.get("volume"))
                .destinationStationCode((String) data.get("destination_station_code"))
                .sourceStationCode((String) data.get("source_station_code"))
                .cargoCode((String) data.get("cargo_code"))
                .requirementOrders((Integer) data.get("requirement_orders"))
                .build();
    }

    private TariffRequest toTariffRequest(FlightAddressing flightAddressing) {
        return TariffRequest.builder()
                .id(Long.toString(flightAddressing.getId()))
                .departureStation(flightAddressing.getCurrentFlightDestStationCode())
                .destinationStation(flightAddressing.getSourceStationCode())
                .cargo(flightAddressing.getCargoCode())
                .wagonType(flightAddressing.getWagonType())
                .volune(flightAddressing.getVolume().toString())
                .flightType(flightAddressing.getLoaded())
                .url(TARIFF_CALLBACK_URL)
                .build();
    }

    private RateRequest toRateRequest(FlightAddressing flightAddressing){
        return RateRequest.builder()
                .id(Long.toString(flightAddressing.getId()))
                .departureStation(flightAddressing.getSourceStationCode())
                .destinationStation(flightAddressing.getDestinationStationCode())
                .cargo(flightAddressing.getCargoCode())
                .wagonType(flightAddressing.getWagonType())
                .volune(flightAddressing.getVolume().toString())
                .datefrom(flightAddressing.getDateFrom())
                .dateto(flightAddressing.getDateTo())
                .url(RATE_CALLBACK_URL)
                .build();
    }
}
