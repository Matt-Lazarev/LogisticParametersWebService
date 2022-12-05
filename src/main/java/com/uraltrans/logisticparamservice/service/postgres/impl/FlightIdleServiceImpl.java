package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.idle.FlightIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.entity.postgres.FlightIdle;
import com.uraltrans.logisticparamservice.service.mapper.FlightIdleMapper;
import com.uraltrans.logisticparamservice.repository.postgres.FlightIdleRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightIdleService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightService;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import com.uraltrans.logisticparamservice.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightIdleServiceImpl implements FlightIdleService {

    private final FlightIdleRepository flightIdleRepository;
    private final FlightService flightService;
    private final FlightIdleMapper mapper;

    @Override
    public List<FlightIdleDto> getAllLoadingUnloadingIdles() {
        return mapper.mapToListDto(flightIdleRepository.findAll());
    }

    @Override
    public void saveAll(LoadParameters dto) {
        prepareNextSave();

        List<Flight> allFlights = flightService.getAllFlights();

        allFlights = filterDuplicateFlights(allFlights);
        allFlights = calculateLoadAndUnloadTime(allFlights);
        filterFlights(allFlights, dto);
        flightService.saveAll(allFlights);

        List<FlightIdle> data = mapper.mapToLoadingUnloadingList(
                flightService.getGroupedCarLoadIdle(), flightService.getGroupCarUnloadIdle());
        data = filterFlightIdles(data);
        flightIdleRepository.saveAll(data);
    }

    private void prepareNextSave() {
        flightIdleRepository.truncate();
    }

    private List<Flight> calculateLoadAndUnloadTime(List<Flight> flights) {
        List<Flight> loadedSortedFlights = flights
                .stream()
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

        if (isBorderCrossingFlights(prevFlight, currFlight)) {
            currFlight.setCarLoadIdleDays(null);
            return;
        }

        if (isDualFlights(prevFlight, currFlight)) {
            prevFlight.setComment("сдвойка");
            prevFlight.setCarLoadIdleDays(null);
        }

        if (sendDate != null && arriveToSourceStation != null) {
            long loadDays = ChronoUnit.DAYS.between(Mapper.toLocalDate(arriveToSourceStation), Mapper.toLocalDate(sendDate));
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

        if (isBorderCrossingFlights(prevFlight, currFlight)) {
            currFlight.setCarUnloadIdleDays(null);
            return;
        }

        if (isDualFlights(currFlight, nextFlight)) {
            currFlight.setCarUnloadIdleDays(null);
            currFlight.setComment("сдвойка");
            return;
        }

        if (nextFlightStart != null && arriveToDestStation != null) {
            long unloadDays = ChronoUnit.DAYS.between(Mapper.toLocalDate(arriveToDestStation), Mapper.toLocalDate(nextFlightStart));
            currFlight.setCarUnloadIdleDays((int) unloadDays);
        }
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

    private boolean isDualFlights(Flight first, Flight second) {
        return first != null && second != null && first.getDestStation().equalsIgnoreCase(second.getSourceStation());
    }

    public List<Flight> filterDuplicateFlights(List<Flight> flights) {
        List<Flight> sortedFlights = flights
                .stream()
                .filter(flight -> flight.getLoaded().equalsIgnoreCase("груж"))
                .filter(flight -> flight.getCarType() != null && flight.getCarType().equalsIgnoreCase("кр"))
                .sorted(this::compareBySendDateAndInvNumberAndCarNumber)
                .collect(Collectors.toList());

        for (int i = 0; i < sortedFlights.size() - 1; i++) {
            Flight current = sortedFlights.get(i);
            Flight lastDuplicate = null;
            while (i < sortedFlights.size() - 1 && compareInvNumberAndCarNumber(current, sortedFlights.get(i + 1))) {
                lastDuplicate = sortedFlights.get(i + 1);
                sortedFlights.remove(i + 1);
            }

            if (lastDuplicate != null && lastDuplicate.getArriveToDestStationDate() != null) {
                current.setArriveToDestStationDate(lastDuplicate.getArriveToDestStationDate());
            }
            if (lastDuplicate != null && lastDuplicate.getNextFlightStartDate() != null) {
                current.setNextFlightStartDate(lastDuplicate.getNextFlightStartDate());
            }
            if (lastDuplicate != null && lastDuplicate.getArriveToDestStationDate() != null) {
                current.setArriveToDestStationDate(lastDuplicate.getArriveToDestStationDate());
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

        if (o1.getSendDate().equals(o2.getSendDate())) {
            return invNumberAndCarNumber1.compareTo(invNumberAndCarNumber2);
        }
        return o1.getSendDate().compareTo(o2.getSendDate());
    }

    private void filterFlights(List<Flight> allFlights, LoadParameters dto) {
        List<String> discardedFlights = new ArrayList<>();
        allFlights
                .stream()
                .filter(flight -> flight.getCarLoadIdleDays() != null &&
                        (flight.getCarLoadIdleDays() <=  dto.getMinLoadIdleDays() ||
                                flight.getCarLoadIdleDays() > dto.getMaxLoadIdleDays()))
                .forEach(flight -> {
                    discardedFlights.add(flight + " --- (превышен лимит времени погрузки)");
                    flight.setCarLoadIdleDays(null);
                });
        allFlights
                .stream()
                .filter(flight -> flight.getCarUnloadIdleDays() != null &&
                        (flight.getCarUnloadIdleDays() <= dto.getMinUnloadIdleDays() ||
                                flight.getCarUnloadIdleDays() > dto.getMaxUnloadIdleDays()))
                .forEach(flight -> {
                    discardedFlights.add(flight + " --- (превышен лимит времени выгрузки)");
                    flight.setCarUnloadIdleDays(null);
                });

        FileUtils.writeDiscardedFlights(discardedFlights, true);
    }

    private List<FlightIdle> filterFlightIdles(List<FlightIdle> flightIdles){
        return flightIdles
                .stream()
                .filter(f -> f.getLoading() != null || f.getUnloading() != null)
                .filter(f -> f.getVolume() != null)
                .collect(Collectors.toList());
    }
}
