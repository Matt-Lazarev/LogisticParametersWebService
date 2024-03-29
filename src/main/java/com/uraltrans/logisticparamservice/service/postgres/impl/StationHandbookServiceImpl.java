package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uraltrans.logisticparamservice.dto.geocode.Countries;
import com.uraltrans.logisticparamservice.dto.geocode.Station;
import com.uraltrans.logisticparamservice.dto.station.StationResponse;
import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;
import com.uraltrans.logisticparamservice.entity.utcsrs.projection.UtcsrsStationHandbookProjection;
import com.uraltrans.logisticparamservice.exception.StationsNotFoundException;
import com.uraltrans.logisticparamservice.repository.postgres.StationHandbookRepository;
import com.uraltrans.logisticparamservice.repository.utcsrs.UtcsrsStationHandbookRepository;
import com.uraltrans.logisticparamservice.service.mapper.mapstruct.UtcsrsStationHandbookMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.StationHandbookService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StationHandbookServiceImpl implements StationHandbookService {
    private static final String API_YANDEX_STATION_LIST_URL = "https://api.rasp.yandex.net/v3.0/stations_list/?apikey=%s";

    private final StationHandbookRepository stationHandbookRepository;
    private final UtcsrsStationHandbookRepository utcsrsStationHandbookRepository;
    private final LoadParameterService loadParameterService;
    private final UtcsrsStationHandbookMapper utcsrsStationHandbookMapper;

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void saveAll() {
        prepareNextSave();
        List<UtcsrsStationHandbookProjection> stations = utcsrsStationHandbookRepository.getAllStations();
        List<StationHandbook> stationHandbook = utcsrsStationHandbookMapper.toStationHandbookList(stations);
        removeDuplicates(stationHandbook);
        updateCoordinatesFromYandexStationList(stationHandbook);
        stationHandbookRepository.saveAll(stationHandbook);
    }

    @Override
    public List<StationHandbook> getAll() {
        return stationHandbookRepository.findAll();
    }

    @Override
    public List<StationResponse> getAllResponses() {
        List<StationResponse> responses = utcsrsStationHandbookMapper.toStationResponseList(stationHandbookRepository.findAll());
        if (responses.size() == 0) {
            throw new StationsNotFoundException("Станции не были найдены. Повторите запрос позже");
        }
        return responses;
    }

    @Override
    public String getRegionByCode6(String code) {
        return stationHandbookRepository.findRegionByCode6(code);
    }

    @Override
    public StationHandbook findStationByCode6(String code) {
        return stationHandbookRepository.findStationByCode6(code);
    }

    @Override
    public String getRegionByStation(String station) {
        return stationHandbookRepository.findRegionByStation(station);
    }

    @Override
    public List<StationHandbook> getStationByName(String station) {
        return stationHandbookRepository.findStationByName(station);
    }

    private void prepareNextSave() {
        stationHandbookRepository.truncate();
    }

    @SneakyThrows
    private void updateCoordinatesFromYandexStationList(List<StationHandbook> stationHandbook) {
        Map<String, Station> yandexStations = getYandexStations();
        stationHandbook
                .stream()
                .filter(s -> s.getLongitude().isEmpty() || s.getLatitude().isEmpty())
                .forEach(s -> {
                    String code = s.getCode6();
                    Station yandexStation = yandexStations.get(code);
                    if (yandexStation != null) {
                        s.setLongitude(yandexStation.getLongitude());
                        s.setLatitude(yandexStation.getLatitude());
                    }
                });
    }

    private void removeDuplicates(List<StationHandbook> stationHandbook) {
        stationHandbook.sort(Comparator.comparing(StationHandbook::getCode6));
        for (int i = 0; i < stationHandbook.size() - 1; i++) {
            if (stationHandbook.get(i).getCode6().equals(stationHandbook.get(i + 1).getCode6())) {
                fillGaps(stationHandbook.get(i), stationHandbook.get(i + 1));
                stationHandbook.remove(i + 1);
                i--;
            }
        }
    }

    private void fillGaps(StationHandbook sh1, StationHandbook sh2) {
        if (sh1.getLatitude() == null || sh1.getLatitude().isEmpty()) {
            sh1.setLatitude(sh2.getLatitude());
        }
        if (sh1.getLongitude() == null || sh1.getLongitude().isEmpty()) {
            sh1.setLongitude(sh2.getLongitude());
        }
    }

    @SneakyThrows
    private Map<String, Station> getYandexStations() {
        LoadParameters parameters = loadParameterService.getLoadParameters();
        String url = String.format(API_YANDEX_STATION_LIST_URL, parameters.getApikeyStationList());

        String json = restTemplate.getForObject(url, String.class);
        Countries countries = new ObjectMapper().readValue(json, Countries.class);

        return countries.getCountries()
                .stream()
                .flatMap(country -> country.getRegions().stream())
                .flatMap(region -> region.getSettlements().stream())
                .flatMap(settlement -> settlement.getStations().stream())
                .filter(station -> station.getCodes().getEsrCode() != null)
                .collect(Collectors.toMap(
                        station -> station.getCodes().getEsrCode(),
                        station -> station
                ));
    }
}
