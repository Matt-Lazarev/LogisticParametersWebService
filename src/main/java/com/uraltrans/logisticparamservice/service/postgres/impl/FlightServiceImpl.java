package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.idle.LoadIdleDto;
import com.uraltrans.logisticparamservice.dto.idle.UnloadIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.service.mapper.FlightMapper;
import com.uraltrans.logisticparamservice.repository.postgres.FlightRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightService;
import com.uraltrans.logisticparamservice.service.itr.RawFlightService;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final RawFlightService rawFlightService;
    private final FlightMapper flightMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoadIdleDto> getGroupedCarLoadIdle() {
        return flightRepository.groupCarLoadIdle();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnloadIdleDto> getGroupCarUnloadIdle() {
        return flightRepository.groupCarUnloadIdle();
    }

    @Override
    @Transactional
    public void saveAllFlights(LoadParameters dto) {
        prepareNextSave();
        List<Flight> allFlights = getAllFlights(dto);
        allFlights = filterFlights(allFlights);
        flightRepository.saveAll(allFlights);
    }

    @Override
    @Transactional
    public void saveAll(List<Flight> flights) {
        flightRepository.saveAll(flights);
    }

    private List<Flight> getAllFlights(LoadParameters dto) {
        List<Map<String, Object>> rawData = rawFlightService.getAllFlightsBetween(dto.getDaysToRetrieveData());
        return flightMapper.mapRawFlightsDataToFlightsList(rawData);
    }

    private void prepareNextSave() {
        flightRepository.truncate();
    }

    private List<Flight> filterFlights(List<Flight> allFlights) {
        List<String> errorFlights = new ArrayList<>();
        allFlights
                .forEach(f -> {
                    if(f.getCargo() == null){
                        errorFlights.add(f + " --- (груз не найден)");
                    }
                });
        allFlights
                .forEach(f -> {
                    if(f.getSourceStationCode() == null){
                        errorFlights.add(f + " --- (станция отправления не найдена)");
                    }

                    if(f.getDestStationCode() == null){
                        errorFlights.add(f + " --- (станция назначения не найдена)");
                    }

                    if(f.getSourceStationCode() != null && f.getSourceStationCode().length() != 6){
                        errorFlights.add(f + " --- (некорректный код станции отправления)");
                    }

                    if(f.getDestStationCode() != null && f.getDestStationCode().length() != 6){
                        errorFlights.add(f + " --- (некорректный код станции отправления)");
                    }
                });

        FileUtils.writeDiscardedFlights(errorFlights, false);

        return allFlights
                .stream()
                .filter(f -> f.getCargo() != null)
                .collect(Collectors.toList());
    }
}
