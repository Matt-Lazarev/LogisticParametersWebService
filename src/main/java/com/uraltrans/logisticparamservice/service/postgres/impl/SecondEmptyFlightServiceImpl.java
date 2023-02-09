package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.secondempty.SecondEmptyFlightResponse;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.entity.postgres.SecondEmptyFlight;
import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;
import com.uraltrans.logisticparamservice.repository.integration.CarRepairInfoRepository;
import com.uraltrans.logisticparamservice.repository.postgres.SecondEmptyFlightRepository;
import com.uraltrans.logisticparamservice.repository.utcsrs.RegisterSecondEmptyFlightRepository;
import com.uraltrans.logisticparamservice.service.mapper.SecondEmptyFlightMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.SecondEmptyFlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.StationHandbookService;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import com.uraltrans.logisticparamservice.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
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
            "421195", "421208", "421195", "421227", "421208", "391089", "421212", "421231", "421246");//421034

    private final FlightService flightService;
    private final StationHandbookService stationHandbookService;
    private final SecondEmptyFlightRepository secondEmptyFlightRepository;
    private final RegisterSecondEmptyFlightRepository registerSecondEmptyFlightRepository;
    private final SecondEmptyFlightMapper secondEmptyFlightMapper;
    private final CarRepairInfoRepository carRepairInfoRepository;
    private final LoadParameterService loadParameterService;

    @Override
    public List<SecondEmptyFlight> getAllSecondEmptyFlight(){
        return secondEmptyFlightRepository.findAll();
    }

    @Override
    public List<SecondEmptyFlightResponse> getAllSecondEmptyFlightResponses() {
        return secondEmptyFlightMapper.mapToSecondEmptyFlightResponses(secondEmptyFlightRepository.findAll());
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
        List<String> discardedFlights = new ArrayList<>();
        flights = flights
                .stream()
                .filter(SecondEmptyFlight::getIsNotFirstEmpty)
                .filter(f -> {
                    boolean result = "ПОР".equalsIgnoreCase(f.getLoaded());
                    if(!result){
                        discardedFlights.add("AID: " + f.getAID() + ", CarNumber: " + f.getCarNumber() + ", From: " + f.getSourceStation() + ", To: " + f.getDestStation() + " - не порожний рейс");
                    }
                    return result;
                })
                .filter(f -> {
                    boolean result = !f.getSourceStation().equalsIgnoreCase(f.getDestStation());
                    if(!result){
                        discardedFlights.add("AID: " + f.getAID() + ", CarNumber: " + f.getCarNumber() + ", From: " + f.getSourceStation() + ", To: " + f.getDestStation() + " - ст. оправления равна ст. назначения");
                    }
                    return result;
                })
                .filter(f -> {
                    boolean result = !FILTER_CARGO_CODES.contains(f.getCargoCode());
                    if(!result){
                        discardedFlights.add("AID: " + f.getAID() + ", CarNumber: " + f.getCarNumber() + ", From: " + f.getSourceStation() + ", To: " + f.getDestStation() + " - недопустимый код груза " + f.getCargoCode());
                    }
                    return result;
                })
                .filter(f -> {
                    boolean result = f.getSourceContragent() != null &&
                            f.getSourceContragent().equalsIgnoreCase("УРАЛЬСКАЯ ТРАНСПОРТНАЯ КОМПАНИЯ");
                    if(!result){
                        discardedFlights.add("AID: " + f.getAID() + ", CarNumber: " + f.getCarNumber() + ", From: " + f.getSourceStation() + ", To: " + f.getDestStation() + " - недопустимый контрагент " + f.getSourceContragent());
                    }
                    return result;
                })
                .filter(f -> {
                    String[] tag2Values = loadParameterService.getLoadParameters().getFeature2().split("[, ]+");
                    String[] tag22Values = loadParameterService.getLoadParameters().getFeature22().split("[, ]+");

                    boolean result = notInFilterTags(tag2Values, f.getTag2()) && notInFilterTags(tag22Values, f.getTag22());
                    if(!result){
                        discardedFlights.add("AID: " + f.getAID() + ", CarNumber: " + f.getCarNumber() + ", From: " + f.getSourceStation() + ", To: " + f.getDestStation() + " - недопустимое значение Признак2 или Признак22");
                    }
                    return result;
                })
                .filter(f -> {
                    StationHandbook sourceStation = stationHandbookService.findStationByCode6(f.getSourceStationCode());
                    boolean result = sourceStation != null && !sourceStation.getExcludeFromSecondEmptyFlight() && !sourceStation.getLock();
                    if(!result){
                        discardedFlights.add("AID: " + f.getAID() + ", CarNumber: " + f.getCarNumber() + ", From: " + f.getSourceStation() + ", To: " + f.getDestStation() + " - ст. отправления исключена из расчетов");
                    }
                    return result;
                })
                .filter(f -> {
                    StationHandbook destStation = stationHandbookService.findStationByCode6(f.getDestStationCode());

                    boolean result = destStation != null && !destStation.getExcludeFromSecondEmptyFlight() && !destStation.getLock();
                    if(!result){
                        discardedFlights.add("AID: " + f.getAID() + ", CarNumber: " + f.getCarNumber() + ", From: " + f.getSourceStation() + ", To: " + f.getDestStation() + " - ст. назначения исключена из расчетов");
                    }
                    return result;
                })
                .filter(f -> {
                    boolean result = !registerSecondEmptyFlightRepository.containsFlightsByCodes(f.getSourceStationCode(), f.getDestStationCode());
                    if(!result){
                        discardedFlights.add("AID: " + f.getAID() + ", CarNumber: " + f.getCarNumber() + ", From: " + f.getSourceStation() + ", To: " + f.getDestStation() + " - маршрут исключен (реестр)");
                    }
                    return result;

                })
                .filter(f -> {
                    boolean result = notInRepair(f.getCarNumber(), f.getDepartureFromSourceStation());
                    if(!result){
                        discardedFlights.add("AID: " + f.getAID() + ", CarNumber: " + f.getCarNumber() + ", From: " + f.getSourceStation() + ", To: " + f.getDestStation() + " - вагон находился в ремонте (относительно даты отправления с ст. отправления)");
                    }
                    return result;
                })
                .filter(f -> {
                    boolean result = f.getArriveToDestStation() == null && f.getNextInvNumber() == null ||
                            notInRepair(f.getCarNumber(), f.getArriveToDestStation());
                    if(!result){
                        discardedFlights.add("AID: " + f.getAID() + ", CarNumber: " + f.getCarNumber() + ", From: " + f.getSourceStation() + ", To: " + f.getDestStation() + " - вагон находился в ремонте (относительно даты прибытия на ст. назначения)");
                    }
                    return result;
                })
                .collect(Collectors.toList());
        FileUtils.writeDiscardedSecondEmptyFlights(discardedFlights, false);
        return flights;
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

        List<String> discardedFlights = new ArrayList<>();
        for(SecondEmptyFlight flight : secondEmptyFlights){
            SecondEmptyFlight previous = groupedFlights.get(flight.getPrevFlightId());
            if(previous == null){
                continue;
            }

            if(flight.getDestStationCode().equals(previous.getDestStationCode()) ||
               flight.getSourceStationCode().equals(previous.getSourceStationCode()) ||
               flight.getSourceStationCode().equals(previous.getDestStationCode()))
            {
                SecondEmptyFlight duplicate = groupedFlights.get(flight.getPrevFlightId());
                discardedFlights.add("AID: " + duplicate.getAID() + ", CarNumber: " + duplicate.getCarNumber() + " - перестановка");
                groupedFlights.remove(duplicate.getPrevFlightId());
            }
        }

        FileUtils.writeDiscardedSecondEmptyFlights(discardedFlights, true);
        return new ArrayList<>(groupedFlights.values());
    }

    private void prepareNextSave(){
        secondEmptyFlightRepository.truncate();
    }

    private boolean notInRepair(Integer carNumber, Timestamp date){
        Integer repairDaysCheck = loadParameterService.getLoadParameters().getRepairDaysCheck();
        if(date == null){
            return false;
        }
        LocalDate initialDate = Mapper.to1cDate(date);
        LocalDate startDate = initialDate.minusDays(repairDaysCheck);
        LocalDate endDate = initialDate.plusDays(repairDaysCheck);

        LocalDate current = startDate;
        while(!current.isEqual(endDate)){
            Map<String, Object> repairInfo = carRepairInfoRepository.getCarRepairByDate(
                    current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), carNumber);
            if (!repairInfo.isEmpty() &&
                    (((byte[]) repairInfo.get("NonworkingPark"))[0] == 1 || ((byte[]) repairInfo.get("RequiresRepair"))[0] == 1)){
                return false;
            }
            current = current.plusDays(1);
        }
        return true;
    }

    private boolean notInFilterTags(String[] tagValues, String flightTag){
        if(flightTag == null || flightTag.isEmpty()){
            return true;
        }
        for(String tagValue: tagValues){
            if(flightTag.toLowerCase().contains(tagValue)){
                return false;
            }
        }
        return true;
    }
}
