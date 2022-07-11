package com.uraltrans.logisticparamservice.service.impl;

import com.uraltrans.logisticparamservice.dto.LoadDataRequestDto;
import com.uraltrans.logisticparamservice.dto.LoadIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.LoadingUnloadingIdle;
import com.uraltrans.logisticparamservice.dto.UnloadIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.mapper.FlightMapper;
import com.uraltrans.logisticparamservice.repository.postgres.FlightRepository;
import com.uraltrans.logisticparamservice.service.abstr.FlightService;
import com.uraltrans.logisticparamservice.service.abstr.LoadingUnloadingIdleService;
import com.uraltrans.logisticparamservice.service.abstr.RawFlightService;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final RawFlightService rawFlightService;
    private final LoadingUnloadingIdleService loadingUnloadingIdleService;
    private final FlightMapper flightMapper;

    @Override
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    @Override
    public void saveAllFlights(LoadDataRequestDto dto) {
        prepareNextSave();

        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(dto.getDaysToRetrieveData());
        List<Map<String, Object>> rawData = rawFlightService.getAllFlightsBetween(from, to);
        List<Flight> allFlights = flightMapper.mapRawFlightsDataToFlightsList(rawData);

        allFlights = filterDuplicateFlights(allFlights);
        allFlights = calculateLoadAndUnloadTime(allFlights);

        filterFlights(allFlights,
                dto.getMaxLoadIdleDays(), dto.getMaxUnloadIdleDays(),
                dto.getMinLoadIdleDays(), dto.getMinUnloadIdleDays());

        flightRepository.saveAll(allFlights);
        saveLoadingUnloadingIdles();
    }

    private void saveLoadingUnloadingIdles() {
        List<LoadIdleDto> loadIdleDtos = flightRepository.groupCarLoadIdle();
        List<UnloadIdleDto> unloadIdleDtos = flightRepository.groupCarUnloadIdle();
        List<LoadingUnloadingIdle> data = flightMapper.mapToLoadingUnloadingList(loadIdleDtos, unloadIdleDtos);
        loadingUnloadingIdleService.saveAll(data);
    }

    private void prepareNextSave() {
        flightRepository.deleteAll();
        loadingUnloadingIdleService.deleteAll();
    }

    private List<Flight> calculateLoadAndUnloadTime(List<Flight> flights) {
        List<Flight> loadedSortedFlights = flights
                .stream()
                .filter(flight -> flight.getLoaded().equalsIgnoreCase("груж"))
                .filter(flight -> flight.getCarType() != null && flight.getCarType().equalsIgnoreCase("кр"))
                .sorted(Comparator.comparing(Flight::getAid))
                .collect(Collectors.toList());

        for (int i = 0; i < loadedSortedFlights.size(); i++) {
            Flight prevFlight = i != 0 ? loadedSortedFlights.get(i - 1) : null;

            Flight nextFlight = i != loadedSortedFlights.size() - 1 ? loadedSortedFlights.get(i + 1) : null;

            Flight currFlight = loadedSortedFlights.get(i);

            calculateLoadIdleDays(prevFlight, currFlight);
            calculateUnloadIdleDays(prevFlight, currFlight, nextFlight);
        }

        return loadedSortedFlights;
    }

    private void calculateLoadIdleDays(Flight prevFlight, Flight currFlight) {
        Timestamp sendDate = currFlight.getSendDate();
        Timestamp arriveToSourceStation = currFlight.getArriveToSourceStationDate();

        if (isBorderCrossingFlights(prevFlight, currFlight) ) {
            currFlight.setCarLoadIdleDays(null);
            return;
        }

        if (isDualFlights(prevFlight, currFlight)) {
            prevFlight.setComment("сдвойка");
            prevFlight.setCarLoadIdleDays(null);
        }

        if (sendDate != null && arriveToSourceStation != null) {
            long loadDays = ChronoUnit.DAYS.between(toLocalDate(arriveToSourceStation), toLocalDate(sendDate));
            currFlight.setCarLoadIdleDays((int) loadDays);
        }
    }

    private void calculateUnloadIdleDays(Flight prevFlight, Flight currFlight, Flight nextFlight) {
        Timestamp arriveToDestStation = currFlight.getArriveToDestStationDate();
        Timestamp nextFlightStart = currFlight.getNextFlightStartDate();

        if (isBorderCrossingFlights(currFlight, nextFlight)) {
            arriveToDestStation = nextFlight.getArriveToDestStationDate();
            nextFlightStart = nextFlight.getNextFlightStartDate();
        }

        if (isBorderCrossingFlights(prevFlight, currFlight) ) {
            currFlight.setCarUnloadIdleDays(null);
            return;
        }

        if (isDualFlights(currFlight, nextFlight)) {
            currFlight.setCarUnloadIdleDays(null);
            currFlight.setComment("сдвойка");
            return;
        }

        if (nextFlightStart != null && arriveToDestStation != null) {
            long unloadDays = ChronoUnit.DAYS.between(toLocalDate(arriveToDestStation), toLocalDate(nextFlightStart));
            currFlight.setCarUnloadIdleDays((int) unloadDays);
        }
    }

    private LocalDate toLocalDate(Timestamp timestamp) {
        return timestamp.toLocalDateTime().toLocalDate();
    }

    private boolean isBorderCrossingFlights(Flight first, Flight second) {
        if (first == null || second == null) {
            return false;
        }

        if (first.getCarNumber() == null || first.getDestStation() == null) {
            return false;
        }

        return first.getAid().equals(second.getAid() - 1)
                && first.getCarNumber().equals(second.getCarNumber())
                && first.getDestStation().equals(second.getDestStation())
                && second.getInvNumber() == null;
    }

    private boolean isDualFlights(Flight first, Flight second){
        return first != null && second != null && first.getDestStation().equalsIgnoreCase(second.getSourceStation());
    }

    private void filterFlights(List<Flight> allFlights,
                               Integer maxLoadIdle, Integer maxUnloadIdle, Integer minLoadIdle, Integer minUnloadIdle) {
         List<String> discardedFlights = new ArrayList<>();
         allFlights
                .stream()
                .filter(flight -> flight.getCarLoadIdleDays() != null &&
                        (flight.getCarLoadIdleDays() <= minLoadIdle || flight.getCarLoadIdleDays() > maxLoadIdle))
                 .forEach(flight -> {
                     discardedFlights.add(flight.toString());
                     flight.setCarLoadIdleDays(null);
                 });
        allFlights
                .stream()
                .filter(flight -> flight.getCarUnloadIdleDays() != null &&
                        (flight.getCarUnloadIdleDays() <= minUnloadIdle || flight.getCarUnloadIdleDays() > maxUnloadIdle))
                .forEach(flight -> {
                    discardedFlights.add(flight.toString());
                    flight.setCarUnloadIdleDays(null);
                });

        FileUtils.writeDiscardedFlights(discardedFlights);
    }

    public List<Flight> filterDuplicateFlights(List<Flight> flights){
        List<Flight> sortedFlights = flights
                .stream()
                .sorted(this::compareBySendDateAndInvNumberAndCarNumber)
                .collect(Collectors.toList());

        for(int i=0; i<sortedFlights.size()-1; i++){
            Flight current = sortedFlights.get(i);
            Flight lastDuplicate = null;
            while(i < sortedFlights.size()-1 && compareInvNumberAndCarNumber(current, sortedFlights.get(i+1))){
                lastDuplicate = sortedFlights.get(i+1);
                sortedFlights.remove(i+1);
            }

            if(lastDuplicate != null && lastDuplicate.getArriveToDestStationDate() != null){
                current.setArriveToDestStationDate(lastDuplicate.getArriveToDestStationDate());
            }
            if(lastDuplicate != null && lastDuplicate.getNextFlightStartDate() != null){
                current.setNextFlightStartDate(lastDuplicate.getNextFlightStartDate());
            }
        }
        return sortedFlights;
    }

    private boolean compareInvNumberAndCarNumber(Flight first, Flight second) {
        return (first.getInvNumber() + first.getCarNumber()).equals(second.getInvNumber() + second.getCarNumber());
    }

    private int compareBySendDateAndInvNumberAndCarNumber(Flight o1, Flight o2) {
        String invNumberAndCarNumber1 = o1.getInvNumber() + o1.getCarNumber();
        String invNumberAndCarNumber2 = o2.getInvNumber() + o2.getCarNumber();

        if(o1.getSendDate().equals(o2.getSendDate())){
            return invNumberAndCarNumber1.compareTo(invNumberAndCarNumber2);
        }
        return o1.getSendDate().compareTo(o2.getSendDate());
    }
}
