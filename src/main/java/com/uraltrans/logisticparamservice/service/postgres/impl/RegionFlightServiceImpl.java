package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.ratetariff.RateTariffConfirmResponse;
import com.uraltrans.logisticparamservice.dto.ratetariff.TariffRequest;
import com.uraltrans.logisticparamservice.dto.ratetariff.TariffResultResponse;
import com.uraltrans.logisticparamservice.dto.regionsegmentation.Flight;
import com.uraltrans.logisticparamservice.dto.regionsegmentation.FlightGroupKey;
import com.uraltrans.logisticparamservice.entity.postgres.*;
import com.uraltrans.logisticparamservice.repository.postgres.RegionFlightRepository;
import com.uraltrans.logisticparamservice.service.mapper.mapstruct.RegionFlightMapper;
import com.uraltrans.logisticparamservice.service.mapper.mapstruct.TariffMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.*;
import com.uraltrans.logisticparamservice.utils.CsvUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionFlightServiceImpl implements RegionFlightService {
    public static final String TARIFF_CALC_URL = "http://10.168.0.8/utc_srs/hs/calc/emptyflight";

    @Value("${application.address}/segmentation/tariff")
    private String tariffCallbackUrl;

    private final RegionSegmentationParametersService regionSegmentationParametersService;
    private final RegionSegmentationLogService regionSegmentationLogService;
    private final StationHandbookService stationHandbookService;
    private final FlightTimeDistanceService flightTimeDistanceService;
    private final FlightIdleService flightIdleService;
    private final RegionFlightRepository regionFlightRepository;
    private final TariffMapper tariffMapper;
    private final RegionFlightMapper regionFlightMapper;
    private final RestTemplate restTemplate;

    @Override
    public List<RegionFlight> getAllRegionFlights() {
        return regionFlightRepository.findAll();
    }

    @Override
    public void saveAllRegionFlights(String logId, boolean scheduled) {
        prepareNextSave();

        RegionSegmentationParameters parameters = regionSegmentationParametersService.getParameters();
        String sourceDataT15 = parameters.getSourceDataT15();
        String loadedFlightsFilename = sourceDataT15 + parameters.getLoadedFlightsFilename();
        String emptyFlightsFilename = sourceDataT15 + parameters.getEmptyFlightsFilename();

        List<Flight> loadedFlights = loadFlightsFromFile(loadedFlightsFilename, "Груженый", logId);
        List<Flight> emptyFlights = loadFlightsFromFile(emptyFlightsFilename, "Порожний", logId);

        loadedFlights = filterFlightsByDate(loadedFlights, parameters, logId);
        emptyFlights = filterFlightsByDate(emptyFlights, parameters, logId);

        List<Flight> loadedFlightsGrouped = groupFlightsByStations(loadedFlights, logId);
        List<Flight> emptyFlightsGrouped = groupFlightsByStations(emptyFlights, logId);

        List<Flight> filteredLoadedFlights = filterFlightsByFlightsAmount(loadedFlightsGrouped, parameters, logId);
        List<Flight>  filteredEmptyFlights = filterFlightsByFlightsAmount(emptyFlightsGrouped, parameters, logId);

        Set<String> notFoundStations = new HashSet<>();
        loadRegionByStations(filteredLoadedFlights, logId, notFoundStations);
        loadRegionByStations(filteredEmptyFlights, logId, notFoundStations);

        filteredLoadedFlights = filterFlightsByRegion(filteredLoadedFlights,  logId);
        filteredEmptyFlights = filterFlightsByRegion(filteredEmptyFlights,  logId);

        loadIdleDays(filteredLoadedFlights, parameters);
        loadIdleDays(filteredEmptyFlights, parameters);

        List<RegionFlight> loadedRegionFlights = regionFlightMapper.toRegionFlightsList(filteredLoadedFlights);
        List<RegionFlight> emptyRegionFlights = regionFlightMapper.toRegionFlightsList(filteredEmptyFlights);

        List<RegionFlight> resultRegionFlights = concatFlights(Arrays.asList(loadedRegionFlights, emptyRegionFlights));
        setIds(resultRegionFlights);

        loadTravelDays(resultRegionFlights, logId);
        regionFlightRepository.saveAll(resultRegionFlights);
    }

    @Override
    public void updateTravelTime(TariffResultResponse response) {
        response.getDetails()
                .forEach(detail -> regionFlightRepository.updateTravelTimeById(detail.getTravelTime(), Integer.parseInt(detail.getId())));
    }

    private void prepareNextSave(){
        regionFlightRepository.deleteAll();
    }

    private List<Flight> loadFlightsFromFile(String filename, String flightType, String logId){
        List<Flight> flights = CsvUtils.readCsvFile(filename)
                .stream()
                .map(row -> flightType.equals("Груженый")
                            ? new Flight(row[0], row[1], row[2], row[3], row[7], row[8], flightType)
                            : new Flight(row[0], row[1], row[2], row[3], row[5], row[4], flightType))
                .collect(Collectors.toList());

        String message = String.format("[Загрузка из файла]: %s рейсов = %d", flightType.equals("Груженый") ? "Груженых" : "Порожних", flights.size());
        regionSegmentationLogService.updateLogMessageById(logId, message);
        return flights;
    }

    private List<Flight> filterFlightsByDate(List<Flight> flights, RegionSegmentationParameters parameters, String logId){
        List<Flight> filteredFlights = flights
                .stream()
                .filter(f -> f.getDepartureDate() != null && f.getDepartureDate().isAfter(LocalDate.now().minusDays(parameters.getDaysToRetrieveLoadedFlights())))
                .collect(Collectors.toList());

        String message = String.format("[Фильтр по дате]: %s рейсов = %d", filteredFlights.get(0).getType().equals("Груженый") ? "Груженых" : "Порожних", filteredFlights.size());
        regionSegmentationLogService.updateLogMessageById(logId, message);
        return filteredFlights;
    }


    private List<Flight> groupFlightsByStations(List<Flight> flights, String logId){
        List<Flight> groupedFlights = flights
                .stream()
                .collect(Collectors.groupingBy(f -> new FlightGroupKey(f.getVolume(), f.getSourceStation(), f.getDestStation(), f.getType())))
                .entrySet()
                .stream()
                .collect(ArrayList::new,
                        (resultList, e) ->
                                resultList.add(new Flight(e.getKey().getVolume(), e.getKey().getSourceStation(), e.getKey().getDestStation(), e.getKey().getType(),
                                        BigDecimal.valueOf(e.getValue().stream().mapToDouble(f -> f.getRateTariff().doubleValue()).sum()),
                                        e.getValue().stream().mapToInt(Flight::getFlightsAmount).sum())),
                        ArrayList::addAll);

        String message = String.format("[Группировка по станциям и объему]: %s рейсов = %d", groupedFlights.get(0).getType().equals("Груженый") ? "Груженых" : "Порожних", groupedFlights.size());
        regionSegmentationLogService.updateLogMessageById(logId, message);
        return groupedFlights;
    }

    private List<Flight> filterFlightsByFlightsAmount(List<Flight> flights, RegionSegmentationParameters parameters, String logId){
        List<Flight> filteredFlights = flights
                .stream()
                .filter(f -> f.getFlightsAmount() >= parameters.getMinLoadedFlightsAmount())
                .collect(Collectors.toList());

        String message = String.format("[Фильтр по количеству рейсов]: %s рейсов = %d", filteredFlights.get(0).getType().equals("Груженый") ? "Груженых" : "Порожних", filteredFlights.size());
        regionSegmentationLogService.updateLogMessageById(logId, message);

        return filteredFlights;
    }

    private void loadRegionByStations(List<Flight> flights, String logId, Set<String> notFoundStations){
        List<String> logs = new ArrayList<>();
        flights
                .forEach(f -> {
                    loadRegionByStation(f.getSourceStation(), f, notFoundStations, logs, "отправления");
                    loadRegionByStation(f.getDestStation(), f, notFoundStations, logs, "назначения");
                });
        regionSegmentationLogService.updateLogMessageById(logId, logs);
    }

    private void loadRegionByStation(String stationName, Flight currentFlight, Set<String> notFoundStations,
                                    List<String> logs, String stationType){
        List<StationHandbook> stations = stationHandbookService.getStationByName(stationName);
        if(stations.size() == 1){
            StationHandbook station = stations.get(0);

            String region = null;
            if(station.getRegion() != null){
                region = station.getRegion();
            }
            else if(station.getRoad() != null){
                region = station.getRoad();
            }
            else{
                if(!notFoundStations.contains(stationName)){
                    String message = String.format("[Загрузка параметров станции] Регион для станции '%s' не найден", stationName);
                    logs.add(message);
                    notFoundStations.add(stationName);
                }
            }

            if(stationType.equals("отправления")){
                currentFlight.setSourceStationCode(station.getCode6());
                currentFlight.setSourceRegion(region);
            }
            else{
                currentFlight.setDestStationCode(station.getCode6());
                currentFlight.setDestRegion(region);
            }
        }
        else if (stations.size() > 1){
            if(!notFoundStations.contains(stationName)) {
                String message = String.format("[Загрузка параметров станции] Найдено несколько станций отправления с именем '%s'", stationName);
                logs.add(message);
                notFoundStations.add(stationName);
            }
        }
        else {
            if(!notFoundStations.contains(stationName)){
                String message = String.format("[Загрузка параметров станции] Стация '%s' не найдена", stationName);
                logs.add(message);
                notFoundStations.add(stationName);
            }
        }
    }

    private List<Flight> filterFlightsByRegion(List<Flight> flights, String logId){
        List<Flight> filteredFlights = flights
                .stream()
                .filter(f -> f.getSourceRegion() != null && f.getDestRegion() != null)
                .collect(Collectors.toList());

        String message = String.format("[Фильтр по регионам]: %s рейсов = %d", filteredFlights.get(0).getType().equals("Груженый") ? "Груженых" : "Порожних", filteredFlights.size());
        regionSegmentationLogService.updateLogMessageById(logId, message);

        return filteredFlights;
    }

    private void loadTravelDays(List<RegionFlight> regionFlights, String logId){
        List<String[]> notFoundFlightTimeDistances = new ArrayList<>();
        regionFlights
                .forEach(f -> {
                    Optional<FlightTimeDistance> timeDistanceOptional = flightTimeDistanceService.findByStationCodesAndFlightType(f.getSourceStationCode(), f.getDestStationCode(), f.getType());
                    if(timeDistanceOptional.isPresent()){
                        f.setTravelDays((int) Math.ceil(Double.parseDouble(timeDistanceOptional.get().getTravelTime())));
                    }
                    else {
                        notFoundFlightTimeDistances.add(new String[]{String.valueOf(f.getId()), f.getSourceStationCode(), f.getDestStationCode(), String.valueOf(f.getVolume()), f.getType()});
                    }
                });

        String message1 = String.format("[Расчет времени в пути]: Не найдено информации по %d рейсам", notFoundFlightTimeDistances.size());
        String message2 = String.format("[Расчет времени в пути]: Формирование запроса к 1С на получение времени в пути для %d рейсов", notFoundFlightTimeDistances.size());
        regionSegmentationLogService.updateLogMessageById(logId, Arrays.asList(message1, message2));

        String token = regionSegmentationParametersService.getParameters().getToken();
        Map<String, String> tariffHeaders = getHeaders(logId, token);
        //sendTariffRequest(notFoundFlightTimeDistances, tariffHeaders, logId);
    }

    private Map<String, String>  getHeaders(String uid, String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("uid", uid);
        headers.put("token", token);
        headers.put("url", tariffCallbackUrl);
        return headers;
    }

    private void sendTariffRequest(List<String[]> flights, Map<String, String> headers, String logId) {
        List<TariffRequest> request = tariffMapper.mapToTariffRequests(flights);
        Map<String, Object> namedRequest = new HashMap<>(Collections.singletonMap("details", request));
        namedRequest.putAll(headers);

        RateTariffConfirmResponse response = restTemplate.postForObject(TARIFF_CALC_URL, namedRequest, RateTariffConfirmResponse.class);
        handleTariffConfirmResponse(response, flights, logId);

        String message = String.format("[Расчет времени в пути] Отправлен запрос на расчет тарифа, UID: %s, количество: %d", headers.get("uid"), request.size());
        regionSegmentationLogService.updateLogMessageById(logId, message);
    }

    private void handleTariffConfirmResponse(RateTariffConfirmResponse response, List<String[]> flights, String logId) {
        if(response == null){
            return;
        }
        Map<String, String[]> mapFlights = flights.stream().collect(Collectors.toMap(f -> f[0], Function.identity()));
        List<String> logs = new ArrayList<>();
        response.getDetails()
                .forEach(detail -> {
                    if (detail.getSuccess().equalsIgnoreCase("false")) {
                        String[] flight = mapFlights.get(detail.getId());
                        String message = String.format("[Расчет времени в пути] Запрос на расчет тарифа не принят. Ст. отправления='%s', Ст назначения='%s', Объем='%s'. Причина: '%s'", flight[1], flight[2], flight[3], detail.getErrorText());
                        logs.add(message);
                    }
                });
        regionSegmentationLogService.updateLogMessageById(logId, logs);
    }

    private void loadIdleDays(List<Flight> flights,  RegionSegmentationParameters parameters){
        flights
                .forEach(f -> {
                    int sourceLoadDays = flightIdleService.getLoadIdleByStationCode(f.getSourceStationCode())
                            .map(idle -> (int) Math.ceil(idle))
                            .orElseGet(parameters::getLoadDays);

                    int sourceUnloadDays = flightIdleService.getUnloadIdleByStationCode(f.getSourceStationCode())
                            .map(idle -> (int) Math.ceil(idle))
                            .orElseGet(parameters::getUnloadDays);

                    f.setSourceStationLoadIdleDays(sourceLoadDays);
                    f.setSourceStationUnloadIdleDays(sourceUnloadDays);

                    int destLoadDays = flightIdleService.getLoadIdleByStationCode(f.getDestStationCode())
                            .map(idle -> (int) Math.ceil(idle))
                            .orElseGet(parameters::getLoadDays);

                    int destUnloadDays = flightIdleService.getUnloadIdleByStationCode(f.getDestStationCode())
                            .map(idle -> (int) Math.ceil(idle))
                            .orElseGet(parameters::getUnloadDays);

                    f.setDestStationLoadIdleDays(destLoadDays);
                    f.setDestStationUnloadIdleDays(destUnloadDays);
                });
    }

    private List<RegionFlight> concatFlights(List<List<RegionFlight>> regionFlightsList){
        return regionFlightsList.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    private void setIds(List<RegionFlight> regionFlights){
        AtomicInteger counter = new AtomicInteger(1);
        regionFlights.forEach(f -> f.setId(counter.getAndIncrement()));
    }
}
