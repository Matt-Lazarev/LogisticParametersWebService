package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.SecondEmptyFlight;
import com.uraltrans.logisticparamservice.repository.postgres.SecondEmptyFlightRepository;
import com.uraltrans.logisticparamservice.service.itr.RawFlightService;
import com.uraltrans.logisticparamservice.service.mapper.SecondEmptyFlightMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.SecondEmptyFlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecondEmptyFlightServiceImpl implements SecondEmptyFlightService {
    private final RawFlightService rawFlightService;
    private final SecondEmptyFlightRepository secondEmptyFlightRepository;
    private final SecondEmptyFlightMapper secondEmptyFlightMapper;

    public List<SecondEmptyFlight> getAllSecondEmptyFlight(){
        return secondEmptyFlightRepository.findAll();
    }

    @Override
    public void saveAllSecondEmptyFlights() {
        prepareNextSave();

        List<SecondEmptyFlight> secondEmptyFlights = secondEmptyFlightMapper.mapRawDataToList(
                rawFlightService.getAllFlightsBetween(90));

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
            while (prevFlight != null && "ГРУЖ".equalsIgnoreCase(prevFlight.getLoaded())){
                prevFlight = groupedFlights.get(prevFlight.getPrevFlightId());
            }

            if(prevFlight != null){
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
        long daysBetween = ChronoUnit.DAYS.between(begin, end);
        double hoursBetween = ChronoUnit.HOURS.between(begin, end) / (double) Duration.ofDays(1).toHours();

        return new BigDecimal(daysBetween + "." + Double.toString(hoursBetween).split("\\.")[1])
                .setScale(2, RoundingMode.HALF_UP);
    }

    private List<SecondEmptyFlight> filterFlights(List<SecondEmptyFlight> flights){
        return flights
                .stream()
                .filter(SecondEmptyFlight::getIsNotFirstEmpty)
                .filter(f -> "ПОР".equalsIgnoreCase(f.getLoaded()))
                .filter(f -> f.getIdleDays() != null && f.getIdleDays().doubleValue() >= 0)
                .collect(Collectors.toList());
    }

    private void prepareNextSave(){
        secondEmptyFlightRepository.truncate();
    }
}
