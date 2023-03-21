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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlightAddressingMapper {
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
                .filter(t -> t.getCargo() != null && !t.getCargo().isEmpty())
                .filter(t -> !t.getDepartureStation().equalsIgnoreCase(t.getDestinationStation()))
                .collect(Collectors.toList());
    }

    public List<RateRequest> mapToRateRequests(List<FlightAddressing> flightAddressings){
        return flightAddressings
                .stream()
                .map(this::toRateRequest)
                .filter(t -> t.getDestinationStation() != null)
                .filter(t -> t.getDepartureStation() != null)
                .filter(t -> t.getCargo() != null && !t.getCargo().isEmpty())
                .filter(t -> !t.getDepartureStation().equalsIgnoreCase(t.getDestinationStation()))
                .filter(t -> !t.getDestinationStation().equals("000000"))
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
                .id(Long.toString(flightAddressing.getTariffId()))
                .departureStation(flightAddressing.getCurrentFlightDestStationCode())
                .destinationStation(flightAddressing.getSourceStationCode())
                .cargo(flightAddressing.getCargoCode())
                .wagonType(flightAddressing.getWagonType())
                .volune(flightAddressing.getVolume().toString())
                .flightType("Порожний")
                .build();
    }

    private RateRequest toRateRequest(FlightAddressing flightAddressing){
        return RateRequest.builder()
                .id(Long.toString(flightAddressing.getRateId()))
                .departureStation(flightAddressing.getSourceStationCode())
                .destinationStation(flightAddressing.getDestinationStationCode())
                .cargo(flightAddressing.getClientOrderCargoCode())
                .wagonType(flightAddressing.getWagonType())
                .volune(flightAddressing.getVolume().toString())
                .datefrom(flightAddressing.getDateFrom())
                .dateto(flightAddressing.getDateTo())
                .build();
    }

    public List<AddressingResponse> mapToResponses(List<FlightAddressing> addressings, String requestId) {
        return addressings
                .stream()
                .map(this::toAddressingResponse)
                .peek(f -> f.setId(requestId))
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

    private CarRepairDto mapToCarRepairDto(Map<String, Object> data){
        return CarRepairDto.builder()
                .carNumber((String) data.get("CarNumber"))
                .nonworkingPark(((byte[]) data.get("NonworkingPark"))[0] == 0)
                .refurbished(((byte[]) data.get("Refurbished"))[0] == 0)
                .rejected(((byte[]) data.get("Rejected"))[0] == 0)
                .build();
    }

    private CarThicknessDto mapToCarThicknessDto(Map<String, Object> data) {
        return CarThicknessDto.builder()
                .carNumber((String) data.get("CarNumber"))
                .thicknessWheel((BigDecimal) data.get("ThicknessWheel"))
                .thicknessComb((BigDecimal) data.get("ThicknessComb"))
                .build();
    }

    public List<FreeWagonResponse> mapToFreeWagonResponses(List<FlightAddressing> addressings, String requestId){
        return addressings
                .stream()
                .map(this::mapToFreeWagon)
                .peek(f -> f.setId(requestId))
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
