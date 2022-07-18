package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.idle.LoadIdleDto;
import com.uraltrans.logisticparamservice.dto.idle.UnloadIdleDto;
import com.uraltrans.logisticparamservice.dto.request.LoadDataRequestDto;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.service.mapper.FlightMapper;
import com.uraltrans.logisticparamservice.repository.postgres.FlightRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightService;
import com.uraltrans.logisticparamservice.service.itr.RawFlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    public void saveAllFlights(LoadDataRequestDto dto) {
        prepareNextSave();
        List<Flight> allFlights = getAllFlights(dto);
        flightRepository.saveAll(allFlights);
    }

    @Override
    @Transactional
    public void saveAll(List<Flight> flights) {
        flightRepository.saveAll(flights);
    }

    private List<Flight> getAllFlights(LoadDataRequestDto dto) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(dto.getDaysToRetrieveData());
        List<Map<String, Object>> rawData = rawFlightService.getAllFlightsBetween(from, to);
        return flightMapper.mapRawFlightsDataToFlightsList(rawData);
    }

    private void prepareNextSave() {
        flightRepository.truncate();
    }
}
