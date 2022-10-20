package com.uraltrans.logisticparamservice.controller;

import com.uraltrans.logisticparamservice.repository.integration.CarRepairInfoRepository;
import com.uraltrans.logisticparamservice.repository.integration.RawDislocationRepository;
import com.uraltrans.logisticparamservice.repository.integration.RawDislocationRepositoryImpl;
import com.uraltrans.logisticparamservice.repository.itr.RawSecondEmptyFlightRepository;
import com.uraltrans.logisticparamservice.repository.postgres.StationHandbookRepository;
import com.uraltrans.logisticparamservice.repository.utcsrs.RawStationHandbookRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.*;
import com.uraltrans.logisticparamservice.service.postgres.impl.SecondEmptyFlightServiceImpl;
import com.uraltrans.logisticparamservice.service.utcsrs.RawStationHandbookService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

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

    final RawDislocationRepository rawDislocationRepository;

    final SecondEmptyFlightServiceImpl secondEmptyFlightServiceImpl;

    final RawSecondEmptyFlightRepository rawSecondEmptyFlightRepository;

    final RawStationHandbookService rawStationHandbookService;

    @GetMapping
    public List<?> getAll() {
        stationHandbookService.saveAll();
        clientOrderService.saveAllClientOrders();
        service.saveAllActualFlights();
        flightRequirementService.saveAllFlightRequirements();
        service.saveAllPotentialFlights();
        flightAddressingService.saveAll();
        return flightAddressingService.getAll();

//        cargoService.saveAll();
//        return cargoService.getAllCargos();



//        secondEmptyFlightServiceImpl.saveAllSecondEmptyFlights();
//        return secondEmptyFlightServiceImpl.getAllSecondEmptyFlight();
//        return carRepairInfoRepository.getAllCarRepairs("4022-09-23");
//        String dislocationDate = LocalDate.now().plusYears(2000).toString();
//        return rawDislocationRepository.getAllDislocations(dislocationDate);
    }


    @GetMapping("/1")
    public List<?> getAll1() {
        secondEmptyFlightServiceImpl.saveAllSecondEmptyFlights();
        return secondEmptyFlightServiceImpl.getAllSecondEmptyFlight();
    }

    @GetMapping("/yandex")
    @SneakyThrows
    public Map<?,?> getGeocoder() {
        //geocodeService.saveGeocodes();
        stationHandbookService.saveAll();
        return geocodeService.getGeocodesCache();
    }
}
