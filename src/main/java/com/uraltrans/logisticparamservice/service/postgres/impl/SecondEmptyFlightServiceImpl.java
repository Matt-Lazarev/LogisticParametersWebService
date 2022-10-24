package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.entity.postgres.SecondEmptyFlight;
import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;
import com.uraltrans.logisticparamservice.repository.integration.CarRepairInfoRepository;
import com.uraltrans.logisticparamservice.repository.postgres.SecondEmptyFlightRepository;
import com.uraltrans.logisticparamservice.service.mapper.SecondEmptyFlightMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.SecondEmptyFlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.StationHandbookService;
import com.uraltrans.logisticparamservice.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecondEmptyFlightServiceImpl implements SecondEmptyFlightService {
    private static final List<String> FILTER_CARGO_CODES = Arrays.asList(
            "421195", "421208", "421195", "421227", "421208", "391089", "421212", "421231", "421246");
    private static final List<String> FILTER_TAG2_VALUES = Arrays.asList("аренда", "воин");

    private final FlightService flightService;
    private final StationHandbookService stationHandbookService;
    private final SecondEmptyFlightRepository secondEmptyFlightRepository;
    private final SecondEmptyFlightMapper secondEmptyFlightMapper;
    private final CarRepairInfoRepository carRepairInfoRepository;
    private final LoadParameterService loadParameterService;

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
        secondEmptyFlights = removeDuplicates(secondEmptyFlights);

        secondEmptyFlights = secondEmptyFlightRepository.saveAll(secondEmptyFlights);
        calculateCountEmptyFlights(secondEmptyFlights);
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
                .filter(f -> {
                    if(f.getTag2() == null || f.getTag2().isEmpty()){
                        return true;
                    }
                    String[] tag2Values = loadParameterService.getLoadParameters().getFeature2().split("[, ]+");
                    for(String tag2Value: tag2Values){
                        if(f.getTag2().toLowerCase().contains(tag2Value)){
                            return false;
                        }
                    }
                    return true;
                })
                .filter(f -> {
                    StationHandbook sourceStation = stationHandbookService.findStationByCode6(f.getSourceStationCode());
                    return sourceStation != null && !sourceStation.getExcludeFromSecondEmptyFlight() && !sourceStation.getLock();
                })
                .filter(f -> {
                    StationHandbook destStation = stationHandbookService.findStationByCode6(f.getDestStationCode());
                    return destStation != null && !destStation.getExcludeFromSecondEmptyFlight() && !destStation.getLock();
                })
                .filter(f -> notInRepair(f.getCarNumber(), f.getDepartureFromSourceStation()))
                .filter(f -> notInRepair(f.getCarNumber(), f.getArriveToDestStation()))
                .collect(Collectors.toList());
    }

    private void calculateCountEmptyFlights(List<SecondEmptyFlight> secondEmptyFlights) {
        secondEmptyFlights
                .forEach(f -> f.setSecondFlights(secondEmptyFlightRepository.countSecondEmptyFlightByCarNumber(f.getCarNumber())));
    }

    private List<SecondEmptyFlight> removeDuplicates(List<SecondEmptyFlight> secondEmptyFlights){
        Map<Integer, SecondEmptyFlight> groupedFlights = secondEmptyFlights
                .stream()
                .collect(Collectors.toMap(
                        SecondEmptyFlight::getAID, f -> f, (a1, a2) -> a1
                ));

        for(SecondEmptyFlight flight : secondEmptyFlights){
            SecondEmptyFlight previous = groupedFlights.get(flight.getPrevFlightId());
            if(previous == null){
                continue;
            }

            if(flight.getDestStationCode().equals(previous.getDestStationCode()) ||
               flight.getSourceStationCode().equals(previous.getSourceStationCode()) ||
               flight.getSourceStationCode().equals(previous.getDestStationCode()))
            {
                groupedFlights.remove(flight.getPrevFlightId());
            }
        }

        return new ArrayList<>(groupedFlights.values());
    }

    private void prepareNextSave(){
        secondEmptyFlightRepository.truncate();
    }

    private boolean notInRepair(Integer carNumber, Timestamp date){
        if(date == null){
            return true;
        }
        if(carNumber.equals(52594538)){
            System.out.println("+");
        }
        Map<String, Object> repairInfo = carRepairInfoRepository.getCarRepairByDate(
                Mapper.to1cDate(date).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), carNumber);
        boolean r = repairInfo == null || (
                ((byte[]) repairInfo.get("NonworkingPark"))[0] == 0 &&
                ((byte[]) repairInfo.get("RequiresRepair"))[0] == 0
        );
        return r;
    }
}
