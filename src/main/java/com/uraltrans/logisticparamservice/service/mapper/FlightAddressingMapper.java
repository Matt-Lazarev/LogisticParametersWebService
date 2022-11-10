package com.uraltrans.logisticparamservice.service.mapper;

import com.uraltrans.logisticparamservice.dto.addressing.AddressingResponse;
import com.uraltrans.logisticparamservice.dto.carinfo.CarRepairDto;
import com.uraltrans.logisticparamservice.dto.carinfo.CarThicknessDto;
import com.uraltrans.logisticparamservice.dto.freewagon.FreeWagonResponse;
import com.uraltrans.logisticparamservice.dto.ratetariff.RateRequest;
import com.uraltrans.logisticparamservice.dto.ratetariff.TariffRequest;
import com.uraltrans.logisticparamservice.entity.postgres.FlightAddressing;
import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
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
                .peek(f -> f.setDislocationStationCode(potentialFlight.getDislocationStationCode()))
                .peek(f -> f.setUtRate(potentialFlight.getUtRate()))
                .peek(f -> f.setFeature2(potentialFlight.getFeature2()))
                .peek(f -> f.setFeature12(potentialFlight.getFeature12()))
                .peek(f -> f.setFeature20(potentialFlight.getFeature20()))
                .peek(f -> f.setClientNextTask(potentialFlight.getClientNextTask()))
                .peek(f -> f.setCarState(potentialFlight.getCarState()))
                .peek(f -> f.setDistanceFromCurrentStation(potentialFlight.getDistanceFromCurrentStation()))
                .peek(f -> f.setDaysBeforeDatePlanRepair(potentialFlight.getDaysBeforeDatePlanRepair()))
                .peek(f -> f.setRestRun(potentialFlight.getRestRun()))
                .collect(Collectors.toList());
    }

    public List<TariffRequest> mapToTariffRequests(List<FlightAddressing> flightAddressings){
        return flightAddressings
                .stream()
                .map(this::toTariffRequest)
                .filter(t -> t.getDestinationStation() != null)
                .filter(t -> t.getDepartureStation() != null)
                .filter(t -> t.getCargo() != null)
                .filter(t -> t.getVolune() != null)
                .collect(Collectors.toList());
    }

    public List<RateRequest> mapToRateRequests(List<FlightAddressing> flightAddressings){
        return flightAddressings
                .stream()
                .map(this::toRateRequest)
                .filter(t -> t.getDestinationStation() != null)
                .filter(t -> t.getDepartureStation() != null)
                .filter(t -> t.getCargo() != null)
                .filter(t -> t.getVolune() != null)
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
                .planOrders((Integer) data.get("in_plan_orders"))
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
                .flightType("Порожний")
                .url(TARIFF_CALLBACK_URL)
                .build();
    }

    private RateRequest toRateRequest(FlightAddressing flightAddressing){
        return RateRequest.builder()
                .id(Long.toString(flightAddressing.getId()))
                .departureStation(flightAddressing.getSourceStationCode())
                .destinationStation(flightAddressing.getDestinationStationCode())
                .cargo(flightAddressing.getClientOrderCargoCode())
                .wagonType(flightAddressing.getWagonType())
                .volune(flightAddressing.getVolume().toString())
                .datefrom(flightAddressing.getDateFrom())
                .dateto(flightAddressing.getDateTo())
                .url(RATE_CALLBACK_URL)
                .build();
    }

    public List<AddressingResponse> mapToResponses(List<FlightAddressing> addressings) {
        return addressings
                .stream()
                .map(this::toAddressingResponse)
                .collect(Collectors.toList());
    }

    private AddressingResponse toAddressingResponse(FlightAddressing flightAddressing) {
        return AddressingResponse.builder()
                .success("true")
                .errorText("")
                .departureStation(flightAddressing.getSourceStationCode())
                .destinationStation(flightAddressing.getDestinationStationCode())
                .cargoId(flightAddressing.getCargoCode())
                .wagonType("Крытый")
                .volume(String.valueOf(flightAddressing.getVolume()))
                .planQuantity(flightAddressing.getPlanOrders())
                .shortage(flightAddressing.getRequirementOrders())
                .carNumber(flightAddressing.getCarNumber())
                .destinationStationCurrentFlight(flightAddressing.getCurrentFlightDestStationCode())
                .dislocationStationCurrentFlight(flightAddressing.getDislocationStationCode())
                .tariff(String.valueOf(flightAddressing.getTariff()))
                .rate(String.valueOf(flightAddressing.getRate()))
                .rateFact(String.valueOf(flightAddressing.getUtRate()))
                .p02(flightAddressing.getFeature2())
                .p12(flightAddressing.getFeature12())
                .p20(flightAddressing.getFeature20())
                .statusWagon(flightAddressing.getCarState())
                .distanceFromCurrentStation(flightAddressing.getDistanceFromCurrentStation())
                .daysBeforeDatePlanRepair(flightAddressing.getDaysBeforeDatePlanRepair())
                .restRun(flightAddressing.getRestRun())
                .nonworkingPark(flightAddressing.getNonworkingPark())
                .refurbished(flightAddressing.getRefurbished())
                .rejected(flightAddressing.getRejected())
                .thicknessComb(flightAddressing.getThicknessComb())
                .thicknessWheel(flightAddressing.getThicknessWheel())
                .build();
    }

    public Map<String, CarRepairDto> mapRawDataToCarRepairMap(List<Map<String, Object>> data){
        return data.stream()
                .map(this::mapToCarRepairDto)
                .collect(Collectors.toMap(
                        CarRepairDto::getCarNumber,
                        x -> x,
                        (cr1, cr2) -> cr1
                ));
    }

    private CarRepairDto mapToCarRepairDto(Map<String, Object> data){
        return CarRepairDto.builder()
                .carNumber((String) data.get("CarNumber"))
                .nonworkingPark(((byte[]) data.get("NonworkingPark"))[0] == 0)
                .refurbished(((byte[]) data.get("Refurbished"))[0] == 0)
                .rejected(((byte[]) data.get("Rejected"))[0] == 0)
                .build();
    }

    public Map<String, CarThicknessDto> mapRawDataToCarThicknessMap(List<Map<String, Object>> data){
        return data.stream()
                .map(this::mapToCarThicknessDto)
                .collect(Collectors.toMap(
                        CarThicknessDto::getCarNumber,
                        x -> x
                ));
    }

    private CarThicknessDto mapToCarThicknessDto(Map<String, Object> data) {
        return CarThicknessDto.builder()
                .carNumber((String) data.get("CarNumber"))
                .thicknessWheel((BigDecimal) data.get("ThicknessWheel"))
                .thicknessComb((BigDecimal) data.get("ThicknessComb"))
                .build();
    }

    public List<FreeWagonResponse> mapToFreeWagonResponses(List<FlightAddressing> addressings){
        return addressings
                .stream()
                .map(this::mapToFreeWagon)
                .collect(Collectors.toList());
    }

    private FreeWagonResponse mapToFreeWagon(FlightAddressing flightAddressing){
        return FreeWagonResponse.builder()
                .success("true")
                .errorText("")
                .departureStation(flightAddressing.getSourceStationCode())
                .destinationStation(flightAddressing.getDestinationStationCode())
                .cargoId(flightAddressing.getCargoCode())
                .wagonType("Крытый")
                .volume(String.valueOf(flightAddressing.getVolume()))
                .carNumber(flightAddressing.getCarNumber())
                .destinationStationCurrentFlight(flightAddressing.getCurrentFlightDestStationCode())
                .dislocationStationCurrentFlight(flightAddressing.getDislocationStationCode())
                .p02(flightAddressing.getFeature2())
                .p12(flightAddressing.getFeature12())
                .p20(flightAddressing.getFeature20())
                .clientNextTask(flightAddressing.getClientNextTask())
                .statusWagon(flightAddressing.getCarState())
                .distanceFromCurrentStation(String.valueOf(flightAddressing.getDistanceFromCurrentStation()))
                .daysBeforeDatePlanRepair(String.valueOf(flightAddressing.getDaysBeforeDatePlanRepair()))
                .restRun(String.valueOf(flightAddressing.getRestRun()))
                .refurbished(flightAddressing.getRefurbished())
                .thicknessComb(String.valueOf(flightAddressing.getThicknessComb()))
                .thicknessWheel(String.valueOf(flightAddressing.getThicknessWheel()))
                .build();
    }
}
