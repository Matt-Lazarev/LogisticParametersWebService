package com.uraltrans.logisticparamservice.controller;

import com.uraltrans.logisticparamservice.repository.integration.CarRepairInfoRepository;
import com.uraltrans.logisticparamservice.repository.integration.RawDislocationRepositoryImpl;
import com.uraltrans.logisticparamservice.repository.utcsrs.RegisterSecondEmptyFlightRepository;
import com.uraltrans.logisticparamservice.repository.postgres.StationHandbookRepository;
import com.uraltrans.logisticparamservice.repository.utcsrs.RawStationHandbookRepository;
import com.uraltrans.logisticparamservice.service.mapper.FlightAddressingMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.*;
import com.uraltrans.logisticparamservice.service.postgres.impl.FlightAddressingServiceImpl;
import com.uraltrans.logisticparamservice.service.postgres.impl.SecondEmptyFlightServiceImpl;
import com.uraltrans.logisticparamservice.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
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

    final RawDislocationRepositoryImpl rawDislocationRepository;

    final RawStationHandbookRepository rep2;

    final GeocodeService geocodeService;

    final StationHandbookRepository stationHandbookRepository;

    final CarRepairInfoRepository carRepairInfoRepository;

    final SecondEmptyFlightServiceImpl secondEmptyFlightServiceImpl;

    final RegisterSecondEmptyFlightRepository registerSecondEmptyFlightRepository;

    private final FlightAddressingMapper flightAddressingMapper;
    private final FlightAddressingServiceImpl flightAddressingServiceImpl;

    @GetMapping
    public Map<?,?> getAll() {
        Map<String, Object> carRepairByDate = carRepairInfoRepository.getCarRepairByDate("4022-12-08", 24346306);
        System.out.println(((byte[])carRepairByDate.get("NonworkingPark"))[0] == 1);
        System.out.println(((byte[])carRepairByDate.get("RequiresRepair"))[0] == 1);
        return carRepairInfoRepository.getCarRepairByDate("4022-12-08", 24346306);
    }

    @GetMapping("/1")
    public List<?> getAll1() {
        return flightAddressingMapper.mapToTariffRequests(
                flightAddressingServiceImpl.groupForTariffRequest(
                        flightAddressingServiceImpl.getAll()));
    }

    @GetMapping("/address")
    public List<?> saveAllAddresses() {
        stationHandbookService.saveAll();
        clientOrderService.saveAllClientOrders();
        service.saveAllActualFlights();
        flightRequirementService.saveAllFlightRequirements();
        service.saveAllPotentialFlights();
        flightAddressingService.saveAll();
        return flightAddressingService.getAll();
    }


    @GetMapping("/yandex")
    public Map<?,?> getGeocoder() {
        stationHandbookService.saveAll();
        return geocodeService.getGeocodesCache();
    }
}
