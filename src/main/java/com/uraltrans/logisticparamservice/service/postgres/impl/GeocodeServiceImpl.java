package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uraltrans.logisticparamservice.entity.postgres.Geocode;
import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;
import com.uraltrans.logisticparamservice.repository.postgres.GeocodeRepository;
import com.uraltrans.logisticparamservice.repository.postgres.StationHandbookRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.GeocodeService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeocodeServiceImpl implements GeocodeService {
    private static final String API_URL = "https://geocode-maps.yandex.ru/1.x?geocode=%s&apikey=%s&format=json";

    private final GeocodeRepository geocodeRepository;
    private final StationHandbookRepository stationHandbookRepository;
    private final LoadParameterService loadParameterService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Map<String, Geocode> getGeocodesCache() {
        return geocodeRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Geocode::getStationCode,
                        geocode -> geocode
                ));
    }

    @Override
    public void saveGeocodes() {
        LoadParameters parameters = loadParameterService.getLoadParameters();
        String apikey = parameters.getApikeyGeocoder();
        Integer amount = parameters.getRequestsAmountInDay();
        List<StationHandbook> stations = filterStations(stationHandbookRepository.findAll(), amount);

        stations.forEach(station -> {
            String geocode = ("железнодорожная станция " + station.getStation()).replace(" ", "+");
            String url = String.format(API_URL, geocode, apikey);
            save(station.getCode6(), url);
        });
        removeExpired();
    }

    @Override
    public Long getGeocodeByStationCode(String stationCode) {
        return geocodeRepository.findGeocodeIdByStationCode(stationCode);
    }

    @SneakyThrows
    private void save(String stationCode, String url) {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JsonNode rootNode = new ObjectMapper().readTree(response.getBody());

        if(!rootNode.findValue("GeocoderResponseMetaData").get("found").textValue().equalsIgnoreCase("1")){
            return;
        }

        if (rootNode.findValue("Point") != null && rootNode.findValue("Point").has("pos")) {
            String[] coordinates = rootNode.findValue("Point").get("pos").textValue().split(" ");
            Geocode geocode = new Geocode(stationCode, coordinates[1], coordinates[0]);

            geocode.setId(geocodeRepository.findGeocodeIdByStationCode(stationCode));
            geocodeRepository.save(geocode);
        }
    }

    public List<StationHandbook> filterStations(List<StationHandbook> stations, int amount) {
        Map<String, Geocode> cache = getGeocodesCache();
        return stations
                .stream()
                .filter(s -> s.getLongitude().isEmpty() || s.getLatitude().isEmpty())
                .filter(s -> !cache.containsKey(s.getCode6()))
                .limit(amount)
                .collect(Collectors.toList());
    }

    private void removeExpired() {
        geocodeRepository.deleteWhereExpired(LocalDateTime.now());
    }
}
