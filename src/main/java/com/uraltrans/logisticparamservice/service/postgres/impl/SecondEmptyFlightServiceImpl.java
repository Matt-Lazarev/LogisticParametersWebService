package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.entity.postgres.SecondEmptyFlight;
import com.uraltrans.logisticparamservice.repository.postgres.SecondEmptyFlightRepository;
import com.uraltrans.logisticparamservice.service.itr.RawFlightService;
import com.uraltrans.logisticparamservice.service.mapper.SecondEmptyFlightMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.SecondEmptyFlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecondEmptyFlightServiceImpl implements SecondEmptyFlightService {
    private static final List<String> FILTER_CARGO_CODES = Arrays.asList("421195", "421208");
    private static final List<String> FILTER_TAG2_VALUES = Arrays.asList("аренда", "воин");

    private final FlightService flightService;
    private final SecondEmptyFlightRepository secondEmptyFlightRepository;
    private final SecondEmptyFlightMapper secondEmptyFlightMapper;

    @Override
    public List<SecondEmptyFlight> getAllSecondEmptyFlight(){
        return secondEmptyFlightRepository.findAll();
    }

    @Override
    public void saveAllSecondEmptyFlights() {
        prepareNextSave();

        List<Flight> flights = flightService.getAllFlights();
        List<SecondEmptyFlight> secondEmptyFlights = secondEmptyFlightMapper.mapToSecondEmptyFlight(flights);

        calculatePrevEmptyFlightDates(secondEmptyFlights);
        secondEmptyFlights = filterFlights(secondEmptyFlights);

        secondEmptyFlightRepository.saveAll(secondEmptyFlights);
    }

    private void calculatePrevEmptyFlightDates(List<SecondEmptyFlight> flights){
        Map<Integer, SecondEmptyFlight> groupedFlights = flights
                .stream()
                .collect(Collectors.toMap(
                        SecondEmptyFlight::getAID, f -> f, (a1, a2) -> a1
                ));

        for(SecondEmptyFlight currentFlight : flights){
            SecondEmptyFlight prevFlight = groupedFlights.get(currentFlight.getPrevFlightId());

            if(prevFlight != null && "ПОР".equalsIgnoreCase(prevFlight.getLoaded())){
                currentFlight.setPrevEmptyFlightRegistrationDate(prevFlight.getCurrEmptyFlightRegistrationDate());
                currentFlight.setPrevEmptyFlightArriveAtDestStationDate(prevFlight.getCurrEmptyFlightArriveAtDestStationDate());
            }
        }

        flights
                .stream()
                .filter(f -> f.getPrevEmptyFlightRegistrationDate() != null)
                .filter(f -> f.getPrevEmptyFlightArriveAtDestStationDate() != null)
                .forEach(f -> f.setIdleDays(
                        calculateIdle(f.getPrevEmptyFlightRegistrationDate(), f.getPrevEmptyFlightArriveAtDestStationDate())));
    }

    private BigDecimal calculateIdle(LocalDateTime end, LocalDateTime begin){
        double daysBetween = ChronoUnit.MINUTES.between(begin, end) / (double) Duration.ofDays(1).toMinutes();
        return new BigDecimal(daysBetween).setScale(2, RoundingMode.HALF_UP);
    }

    private List<SecondEmptyFlight> filterFlights(List<SecondEmptyFlight> flights){
        return flights
                .stream()
                .filter(SecondEmptyFlight::getIsNotFirstEmpty)
                .filter(f -> "ПОР".equalsIgnoreCase(f.getLoaded()))
                .filter(f -> f.getIdleDays() != null && f.getIdleDays().doubleValue() >= 0)
                .filter(f -> !f.getSourceStation().equalsIgnoreCase(f.getDestStation()))
                .filter(f -> !FILTER_CARGO_CODES.contains(f.getCargoCode()))
                .filter(f -> f.getSourceContragent() != null &&
                        f.getSourceContragent().equalsIgnoreCase("УРАЛЬСКАЯ ТРАНСПОРТНАЯ КОМПАНИЯ"))
                .filter(f -> f.getTag2() == null || !f.getTag2().toLowerCase().contains(FILTER_TAG2_VALUES.get(0)))
                .filter(f -> f.getTag2() == null || !f.getTag2().toLowerCase().contains(FILTER_TAG2_VALUES.get(1)))
                .collect(Collectors.toList());
    }

    private void prepareNextSave(){
        secondEmptyFlightRepository.truncate();
    }
}
