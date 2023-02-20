package com.uraltrans.logisticparamservice.controller;

import com.uraltrans.logisticparamservice.repository.integration.CarRepairInfoRepository;
import com.uraltrans.logisticparamservice.repository.utcsrs.RawStationHandbookRepository;
import com.uraltrans.logisticparamservice.repository.utcsrs.RegisterSecondEmptyFlightRepository;
import com.uraltrans.logisticparamservice.repository.utcsrs.RegisterSecondEmptyFlightRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final RawStationHandbookRepository rawStationHandbookRepository;

    private final CarRepairInfoRepository carRepairInfoRepository;

    private final RegisterSecondEmptyFlightRepositoryImpl registerSecondEmptyFlightRepository;

    @GetMapping
    public Object getAll() {
//        return rawStationHandbookRepository.getAllStations()
//                        .stream()
//                        .map(map -> map.get("_Code"))
//                        .filter(code -> ((String) code).length() != 6)
//                        .collect(Collectors.toList());
//                        }
       return carRepairInfoRepository.getAllCarRepairs("4022-02-20");
    }
}
