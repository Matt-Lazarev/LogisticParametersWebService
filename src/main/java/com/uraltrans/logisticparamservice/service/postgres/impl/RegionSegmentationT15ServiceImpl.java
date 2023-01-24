package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.ratetariff.RateTariffConfirmResponse;
import com.uraltrans.logisticparamservice.dto.ratetariff.TariffRequest;
import com.uraltrans.logisticparamservice.dto.regionsegmentation.EmptyFlight;
import com.uraltrans.logisticparamservice.dto.regionsegmentation.FlightGroupKey;
import com.uraltrans.logisticparamservice.dto.regionsegmentation.LoadedFlight;
import com.uraltrans.logisticparamservice.entity.postgres.FlightTimeDistance;
import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationParameters;
import com.uraltrans.logisticparamservice.service.mapper.mapstruct.TariffMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightIdleService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightTimeDistanceService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionSegmentationParametersService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionSegmentationT15Service;
import com.uraltrans.logisticparamservice.service.postgres.abstr.StationHandbookService;
import com.uraltrans.logisticparamservice.utils.CsvUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionSegmentationT15ServiceImpl implements RegionSegmentationT15Service {
    public static final String TARIFF_CALC_URL = "http://10.168.0.8/utc_srs/hs/calc/emptyflight";

    @Value("${application.address}/segmentation/tariff")
    private String tariffCallbackUrl;

    private final RegionSegmentationParametersService regionSegmentationParametersService;
    private final StationHandbookService stationHandbookService;
    private final FlightTimeDistanceService flightTimeDistanceService;
    private final FlightIdleService flightIdleService;
    private final TariffMapper tariffMapper;

    private RestTemplate restTemplate = new RestTemplate();

    public void saveAllRegionSegmentationsT15() {
        RegionSegmentationParameters parameters = regionSegmentationParametersService.getParameters();

        Map<FlightGroupKey, List<LoadedFlight>> loadedFlights = CsvUtils.readCsvFile("t2.csv", ";")
                .stream()
                .skip(1)
                .map(row -> new LoadedFlight(row[0], row[1], row[2], row[3], row[4], row[7], row[8]))
                .filter(f -> f.getDepartureDate() != null && f.getDepartureDate().isAfter(LocalDate.now().minusDays(parameters.getDaysToRetrieveLoadedFlights())))
                .collect(Collectors.groupingBy(f -> new FlightGroupKey(f.getVolume(), f.getSourceStation(), f.getDestStation())));

        Map<FlightGroupKey, List<EmptyFlight>> emptyFlights = CsvUtils.readCsvFile("t3.csv", ";")
                .stream()
                .skip(1)
                .map(row -> new EmptyFlight(row[0], row[1], row[2], row[3], row[4], row[5]))
                .filter(f -> f.getDepartureDate() != null && f.getDepartureDate().isAfter(LocalDate.now().minusDays(parameters.getDaysToRetrieveEmptyFlights())))
                .collect(Collectors.groupingBy(f -> new FlightGroupKey(f.getVolume(), f.getSourceStation(), f.getDestStation())));

//        List<LoadedFlight> loadedFlightsGrouped = loadedFlights
//                .entrySet()
//                .stream()
//                .collect(ArrayList::new,
//                        (resultList, e) ->
//                                new LoadedFlight(e.getKey().getVolume(), e.getKey().getSourceStation(), e.getKey().getDestStation(),
//                                BigDecimal.valueOf(e.getValue().stream().mapToDouble(f -> f.getRate().doubleValue()).sum()),
//                                e.getValue().stream().mapToInt(LoadedFlight::getFlightsAmount).sum()),
//                        ArrayList::addAll);
//
//        List<EmptyFlight> emptyFlightsGrouped = emptyFlights
//                .entrySet()
//                .stream()
//                .collect(ArrayList::new,
//                        (resultList, e) ->
//                                new EmptyFlight(e.getKey().getVolume(), e.getKey().getSourceStation(), e.getKey().getDestStation(),
//                                        BigDecimal.valueOf(e.getValue().stream().mapToDouble(f -> f.getTariff().doubleValue()).sum()),
//                                        e.getValue().stream().mapToInt(EmptyFlight::getFlightsAmount).sum()),
//                        ArrayList::addAll);

        List<LoadedFlight> loadedFlightsGrouped = new ArrayList<>();
        loadedFlights
                .forEach((key, value) -> loadedFlightsGrouped.add(
                        new LoadedFlight(key.getVolume(), key.getSourceStation(), key.getDestStation(),
                                BigDecimal.valueOf(value.stream().mapToDouble(f -> f.getRate().doubleValue()).sum()),
                                value.stream().mapToInt(LoadedFlight::getFlightsAmount).sum())));

        List<EmptyFlight> emptyFlightsGrouped = new ArrayList<>();
        emptyFlights
                .forEach((key, value) -> emptyFlightsGrouped.add(
                        new EmptyFlight(key.getVolume(), key.getSourceStation(), key.getDestStation(),
                                BigDecimal.valueOf(value.stream().mapToDouble(f -> f.getTariff().doubleValue()).sum()),
                                value.stream().mapToInt(EmptyFlight::getFlightsAmount).sum())));

        List<LoadedFlight> filteredLoadedFlights = loadedFlightsGrouped
                .stream()
                .filter(f -> f.getFlightsAmount() >= parameters.getMinLoadedFlightsAmount())
                .collect(Collectors.toList());

        List<EmptyFlight>  filteredEmptyFlights = emptyFlightsGrouped
                .stream()
                .filter(f -> f.getFlightsAmount() >= parameters.getMinEmptyFlightsAmount())
                .collect(Collectors.toList());

        loadRegionByStations(filteredLoadedFlights, filteredEmptyFlights);
        loadTravelDays(filteredLoadedFlights, filteredEmptyFlights);
        loadIdleDays(filteredLoadedFlights, filteredEmptyFlights);

        filteredLoadedFlights
                .subList(0, 20)
                .forEach(System.out::println);

        filteredEmptyFlights
                .subList(0, 20)
                .forEach(System.out::println);
    }

    private void loadRegionByStations(List<LoadedFlight> loadedFlights, List<EmptyFlight> emptyFlights){
        loadedFlights
                .forEach(f -> {
                    stationHandbookService.getStationByName(f.getSourceStation())
                            .ifPresent(source -> {
                                f.setSourceRegion(source.getRegion());
                                f.setSourceStationCode(source.getCode6());
                            });

                   stationHandbookService.getStationByName(f.getDestStation())
                           .ifPresent(dest -> {
                               f.setDestRegion(dest.getRegion());
                               f.setDestStationCode(dest.getCode6());
                           });
                });

        emptyFlights
                .forEach(f -> {
                    stationHandbookService.getStationByName(f.getSourceStation())
                            .ifPresent(source -> {
                                f.setSourceRegion(source.getRegion());
                                f.setSourceStationCode(source.getCode6());
                            });

                    stationHandbookService.getStationByName(f.getDestStation())
                            .ifPresent(dest -> {
                                f.setDestRegion(dest.getRegion());
                                f.setDestStationCode(dest.getCode6());
                            });
                });
    }

    private void loadTravelDays(List<LoadedFlight> loadedFlights, List<EmptyFlight> emptyFlights){
        List<String[]> notFoundFlightTimeDistances = new ArrayList<>();
        loadedFlights
                .forEach(f -> {
                    Optional<FlightTimeDistance> timeDistanceOptional = flightTimeDistanceService.findByStationCodesAndFlightType(f.getSourceStationCode(), f.getDestStationCode(), f.getType());
                    if(timeDistanceOptional.isPresent()){
                        f.setTravelDays((int) Math.ceil(Double.parseDouble(timeDistanceOptional.get().getTravelTime())));
                    }
                    else {
                        notFoundFlightTimeDistances.add(new String[]{f.getSourceStationCode(), f.getDestStationCode(), String.valueOf(f.getVolume()), f.getType()});
                    }
                });

        emptyFlights
                .forEach(f -> {
                    Optional<FlightTimeDistance> timeDistanceOptional = flightTimeDistanceService.findByStationCodesAndFlightType(f.getSourceStationCode(), f.getDestStationCode(), f.getType());
                    if(timeDistanceOptional.isPresent()){
                        f.setTravelDays((int) Math.ceil(Double.parseDouble(timeDistanceOptional.get().getTravelTime())));
                    }
                    else {
                        notFoundFlightTimeDistances.add(new String[]{f.getSourceStationCode(), f.getDestStationCode(), String.valueOf(f.getVolume()), f.getType()});
                    }
                });

        String token = regionSegmentationParametersService.getParameters().getToken();
        Map<String, String> tariffHeaders = getHeaders(UUID.randomUUID().toString(), token);
        sendTariffRequest(notFoundFlightTimeDistances, tariffHeaders);
    }

    private Map<String, String>  getHeaders(String uid, String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("uid", uid);
        headers.put("token", token);
        headers.put("url", tariffCallbackUrl);
        return headers;
    }

    private void sendTariffRequest(List<String[]> flights, Map<String, String> headers) {
        List<TariffRequest> request = tariffMapper.mapToTariffRequests(flights);
        Map<String, Object> namedRequest = new HashMap<>(Collections.singletonMap("details", request));
        namedRequest.putAll(headers);

        RateTariffConfirmResponse response = restTemplate.postForObject(TARIFF_CALC_URL, namedRequest, RateTariffConfirmResponse.class);
        handleTariffConfirmResponse(response);

        log.info("Отправлен запрос на расчет тарифа, UID: {}, SIZE: {}", headers.get("uid"), request.size());
    }

    private void handleTariffConfirmResponse(RateTariffConfirmResponse response) {
        if(response == null){
            return;
        }
        List<String> errors = new ArrayList<>();
        response.getDetails()
                .forEach(detail -> {
                    if (detail.getSuccess().equalsIgnoreCase("false")) {
                        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        errors.add("[" + time + "] -> (запрос на расчет тарифа не принят, причина: '" + detail.getErrorText() + ")'");
                        log.error("[{}] -> (запрос на расчет тарифа не принят, причина: '{}')", time, detail.getErrorText());
                    }
                });
        //FileUtils.writeTariffRateErrors(errors, true);
    }

    private void loadIdleDays(List<LoadedFlight> loadedFlights, List<EmptyFlight> emptyFlights){
        RegionSegmentationParameters parameters = regionSegmentationParametersService.getParameters();

        loadedFlights
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

        emptyFlights
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





//        List<LoadedFlight> loadedFlightsGrouped = new ArrayList<>();
//
//        loadedFlights
//                .entrySet()
//                .forEach(e -> loadedFlightsGrouped.add(
//                        new LoadedFlight(e.getKey().getVolume(), e.getKey().getSourceStation(), e.getKey().getDestStation(),
//                                BigDecimal.valueOf(e.getValue().stream().mapToDouble(f -> f.getRate().doubleValue()).sum()),
//                                e.getValue().stream().mapToInt(LoadedFlight::getFlightsAmount).sum())));
}
