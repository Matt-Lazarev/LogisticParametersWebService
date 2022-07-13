package com.uraltrans.logisticparamservice.service.impl;

import com.uraltrans.logisticparamservice.dto.distancetime.FlightTimeDistanceRequest;
import com.uraltrans.logisticparamservice.dto.distancetime.FlightTimeDistanceResponse;
import com.uraltrans.logisticparamservice.dto.request.LoadDataRequestDto;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.entity.postgres.FlightTimeDistance;
import com.uraltrans.logisticparamservice.mapper.FlightTimeDistanceMapper;
import com.uraltrans.logisticparamservice.repository.postgres.FlightRepository;
import com.uraltrans.logisticparamservice.repository.postgres.FlightTimeDistanceRepository;
import com.uraltrans.logisticparamservice.service.abstr.FlightTimeDistanceService;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightTimeDistanceServiceImpl implements FlightTimeDistanceService {
    private final FlightRepository flightRepository;
    private final FlightTimeDistanceRepository flightTimeDistanceRepository;
    private final FlightTimeDistanceMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<FlightTimeDistance> getAllTimeDistances() {
        return flightTimeDistanceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightTimeDistanceResponse> getTimeDistanceResponses(List<FlightTimeDistanceRequest> requests) {
        List<FlightTimeDistanceResponse> responses = new ArrayList<>();
        requests
                .forEach(req -> {
                    FlightTimeDistance timeDistance = flightTimeDistanceRepository.findByStationCodesAndFlightType(
                            req.getDepartureStation(), req.getDestinationStation(), req.getFlightType());
                    FlightTimeDistanceResponse resp = new FlightTimeDistanceResponse();
                    resp.setId(req.getId());
                    if(timeDistance != null){
                        resp.setDistance(timeDistance.getDistance());
                        resp.setTravelTime(timeDistance.getTravelTime());
                        resp.setErrorText("");
                        resp.setSuccess("true");
                    }
                    else {
                        resp.setErrorText("Рейс не найден");
                        resp.setSuccess("false");
                    }
                    responses.add(resp);
                });
        return responses;
    }

    @Override
    @Transactional
    public void saveAll(LoadDataRequestDto dto) {
        prepareNextSave();

        List<Flight> allFlights = flightRepository.findAll();
        calculateFlightDistance(allFlights);
        allFlights = filterFlights(allFlights, dto);

        Map<String, String> distancesLoaded = groupFlightsDistances(allFlights, "груж");
        Map<String, String> distancesUnloaded = groupFlightsDistances(allFlights, "пор");
        Map<String, String> travelTimesLoaded = groupFlightsTravelTimes(allFlights, "груж");
        Map<String, String> travelTimesUnloaded = groupFlightsTravelTimes(allFlights, "пор");
        List<FlightTimeDistance> result = mapper.mapToList(
                distancesLoaded, distancesUnloaded, travelTimesLoaded, travelTimesUnloaded);
        flightTimeDistanceRepository.saveAll(result);
    }

    private void prepareNextSave(){
        flightTimeDistanceRepository.truncate();
    }

    private void calculateFlightDistance(List<Flight> allFlights) {
        allFlights
                .stream()
                .filter(f -> f.getDepartureFromSourceStationDateDate() != null)
                .filter(f -> f.getArriveToDestStationDate() != null)
                .forEach(f -> f.setTravelTime(
                        daysBetween(f.getDepartureFromSourceStationDateDate(), f.getArriveToDestStationDate())));
    }

    private List<Flight> filterFlights(List<Flight> flights, LoadDataRequestDto dto) {
        List<Flight> allFlights =  flights
                .stream()
                .filter(f -> f.getTravelTime() != null)
                .filter(f -> f.getTravelTime().doubleValue() >= dto.getMinTravelTime())
                .filter(f -> f.getTravelTime().doubleValue() <= dto.getMaxTravelTime())
                .collect(Collectors.toList());

        flights.removeAll(allFlights);
        List<String> discardedFlights = flights
                .stream()
                .filter(f -> f.getTravelTime() != null)
                .map(flight -> flight + " --- (превышен лимит времени перевозки)")
                .collect(Collectors.toList());
        FileUtils.writeDiscardedFlights(discardedFlights, true);
        return allFlights;
    }
    private Map<String, String> groupFlightsDistances(List<Flight> flights, String loaded){
        return flights
                .stream()
                .filter(flight -> flight.getLoaded().equalsIgnoreCase(loaded))
                .filter(flight -> flight.getDistance() != null)
                .collect(Collectors.groupingBy(
                        flight -> flight.getSourceStationCode() + " " + flight.getDestStationCode(),
                        Collectors.averagingDouble(flight -> flight.getDistance().doubleValue())
                ))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        v -> round(v.getValue())
                ));
    }

    private Map<String, String> groupFlightsTravelTimes(List<Flight> flights, String loaded){
        return flights
                .stream()
                .filter(flight -> flight.getLoaded().equalsIgnoreCase(loaded))
                .filter(flight -> flight.getDepartureFromSourceStationDateDate() != null)
                .filter(flight -> flight.getArriveToDestStationDate() != null)
                .collect(Collectors.groupingBy(
                        flight -> flight.getSourceStationCode() + " " + flight.getDestStationCode(),
                        Collectors.averagingDouble(flight -> flight.getTravelTime().doubleValue())
                ))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        v -> round(v.getValue())
                ));
    }
    private BigDecimal daysBetween(Timestamp fromDate, Timestamp toDate){
        LocalDateTime from = fromDate.toLocalDateTime();
        LocalDateTime to = toDate.toLocalDateTime();

        long days = ChronoUnit.DAYS.between(from, to);
        double hours = ChronoUnit.HOURS.between(from, to) - Duration.ofDays(days).toHours();

        return BigDecimal.valueOf(days + hours / Duration.ofDays(1).toHours());
    }

    private String round(double num){
        return BigDecimal.valueOf(num).setScale(2, RoundingMode.HALF_UP).toString();
    }
}
