package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.distancetime.FlightTimeDistanceRequest;
import com.uraltrans.logisticparamservice.dto.distancetime.FlightTimeDistanceResponse;
import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.entity.postgres.FlightTimeDistance;
import com.uraltrans.logisticparamservice.exception.RepeatedRequestException;
import com.uraltrans.logisticparamservice.service.mapper.FlightTimeDistanceMapper;
import com.uraltrans.logisticparamservice.repository.postgres.FlightRepository;
import com.uraltrans.logisticparamservice.repository.postgres.FlightTimeDistanceRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightTimeDistanceService;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import com.uraltrans.logisticparamservice.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightTimeDistanceServiceImpl implements FlightTimeDistanceService {
    private static final Set<String> RESPONSES_CACHE = new HashSet<>();

    private final FlightRepository flightRepository;
    private final FlightTimeDistanceRepository flightTimeDistanceRepository;
    private final FlightTimeDistanceMapper mapper;

    @Override
    public List<FlightTimeDistance> getAllTimeDistances() {
        return flightTimeDistanceRepository.findAll();
    }

    @Override
    public List<FlightTimeDistanceResponse> getTimeDistanceResponses(FlightTimeDistanceRequest request) {
        if(request.getUid() != null && !request.getUid().isEmpty() && RESPONSES_CACHE.contains(request.getUid())){
            throw new RepeatedRequestException("Повторный запрос [uid=" + request.getUid() + "]");
        }

        List<FlightTimeDistanceResponse> responses = new ArrayList<>();
        request.getDetails()
                .forEach(req -> {
                    Optional<FlightTimeDistance> timeDistance = flightTimeDistanceRepository.findByStationCodesAndFlightType(
                            req.getDepartureStation(), req.getDestinationStation(), req.getTypeFlight());
                    FlightTimeDistanceResponse resp = new FlightTimeDistanceResponse();
                    resp.setId(req.getId());
                    if(timeDistance.isPresent()){
                        resp.setDistance(timeDistance.get().getDistance());
                        resp.setTravelTime(timeDistance.get().getTravelTime());
                        resp.setErrorText("");
                        resp.setSuccess("true");
                    }
                    else {
                        resp.setErrorText("Рейс не найден");
                        resp.setSuccess("false");
                    }
                    responses.add(resp);
                });

        RESPONSES_CACHE.add(request.getUid());
        return responses;
    }

    @Override
    public void saveAll(LoadParameters dto) {
        prepareNextSave();

        List<Flight> allFlights = flightRepository.findAll();
        calculateFlightDistance(allFlights);
        allFlights = filterFlights(allFlights, dto);

        Map<String, String> distancesLoaded = groupFlightsDistances(allFlights, "Груженый");
        Map<String, String> distancesUnloaded = groupFlightsDistances(allFlights, "Порожний");
        Map<String, String> travelTimesLoaded = groupFlightsTravelTimes(allFlights, "Груженый");
        Map<String, String> travelTimesUnloaded = groupFlightsTravelTimes(allFlights, "Порожний");
        List<FlightTimeDistance> result = mapper.mapToList(
                distancesLoaded, distancesUnloaded, travelTimesLoaded, travelTimesUnloaded);
        flightTimeDistanceRepository.saveAll(result);
    }

    @Override
    public Optional<FlightTimeDistance> findByStationCodesAndFlightType(String departStation, String destStation, String flightType) {
        return flightTimeDistanceRepository.findByStationCodesAndFlightType(departStation, destStation, flightType);
    }

    private void prepareNextSave(){
        flightTimeDistanceRepository.truncate();
    }

    private void calculateFlightDistance(List<Flight> allFlights) {
        allFlights
                .stream()
                .filter(f -> f.getDepartureFromSourceStationDate() != null)
                .filter(f -> f.getArriveToDestStationDate() != null)
                .forEach(f -> f.setTravelTime(
                        daysBetween(f.getDepartureFromSourceStationDate(), f.getArriveToDestStationDate())));
    }

    private List<Flight> filterFlights(List<Flight> flights, LoadParameters dto) {
        List<Flight> allFlights =  flights
                .stream()
                .filter(f -> f.getTravelTime() != null)
                .filter(f -> f.getSourceStationCode() != null && f.getDestStationCode() != null)
                .filter(f -> !Objects.equals(f.getSourceStationCode(), f.getDestStationCode()))
                .filter(f -> f.getTravelTime().doubleValue() >= dto.getMinTravelTime())
                .filter(f -> f.getTravelTime().doubleValue() <= dto.getMaxTravelTime())
                .peek(f -> f.setLoaded(f.getLoaded().equalsIgnoreCase("груж") ? "Груженый" : "Порожний"))
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
                        v -> MappingUtils.round(v.getValue(), 2)
                ));
    }

    private Map<String, String> groupFlightsTravelTimes(List<Flight> flights, String loaded){
        return flights
                .stream()
                .filter(flight -> flight.getLoaded().equalsIgnoreCase(loaded))
                .filter(flight -> flight.getDepartureFromSourceStationDate() != null)
                .filter(flight -> flight.getArriveToDestStationDate() != null)
                .collect(Collectors.groupingBy(
                        flight -> flight.getSourceStationCode() + " " + flight.getDestStationCode(),
                        Collectors.averagingDouble(flight -> flight.getTravelTime().doubleValue())
                ))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        v -> MappingUtils.round(v.getValue(), 2)
                ));
    }
    private BigDecimal daysBetween(Timestamp fromDate, Timestamp toDate){
        LocalDateTime from = fromDate.toLocalDateTime();
        LocalDateTime to = toDate.toLocalDateTime();

        long days = ChronoUnit.DAYS.between(from, to);
        double hours = ChronoUnit.HOURS.between(from, to) - Duration.ofDays(days).toHours();

        return BigDecimal.valueOf(days + hours / Duration.ofDays(1).toHours());
    }
}
