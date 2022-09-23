package com.uraltrans.logisticparamservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uraltrans.logisticparamservice.dto.geocode.Countries;
import com.uraltrans.logisticparamservice.dto.geocode.Station;
import com.uraltrans.logisticparamservice.dto.ratetariff.RateRequest;
import com.uraltrans.logisticparamservice.dto.ratetariff.RateTariffConfirmResponse;
import com.uraltrans.logisticparamservice.dto.ratetariff.TariffRequest;
import com.uraltrans.logisticparamservice.entity.postgres.Geocode;
import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;
import com.uraltrans.logisticparamservice.repository.integration.CarRepairInfoRepository;
import com.uraltrans.logisticparamservice.repository.integration.RawDislocationRepositoryImpl;
import com.uraltrans.logisticparamservice.repository.postgres.StationHandbookRepository;
import com.uraltrans.logisticparamservice.repository.utcsrs.RawStationHandbookRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    final ClientOrderService clientOrderService;
    final CargoService cargoService;

    final ActualFlightService service;

    final FlightRequirementService flightRequirementService;

    final FlightProfitService flightProfitService;

    final RestTemplate restTemplate;

    final StationHandbookService stationHandbookService;

    final FlightAddressingService flightAddressingService;

    final StationHandbookService serv;

    final RawDislocationRepositoryImpl rep;
    final RawStationHandbookRepository rep2;

    final GeocodeService geocodeService;

    final StationHandbookRepository stationHandbookRepository;

    final CarRepairInfoRepository carRepairInfoRepository;

    @GetMapping
    public List<?> getAll() {
        stationHandbookService.saveAll();
        clientOrderService.saveAllClientOrders();
        service.saveAllActualFlights();
        flightRequirementService.saveAllFlightRequirements();
        service.saveAllPotentialFlights();
        flightAddressingService.saveAll();
        return flightAddressingService.getAll();
       // return carRepairInfoRepository.getAllCarRepairs("4022-09-23");
    }


    @GetMapping("/1")
    public List<?> getAll1() {
        clientOrderService.saveAllClientOrders();
        return clientOrderService.getAllClientOrders();
    }

    @GetMapping("/yandex")
    @SneakyThrows
    public Map<?,?> getGeocoder() {
        //geocodeService.saveGeocodes();
        stationHandbookService.saveAll();
        return geocodeService.getGeocodesCache();
    }
}
