package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.idle.LoadIdleDto;
import com.uraltrans.logisticparamservice.dto.idle.UnloadIdleDto;
import com.uraltrans.logisticparamservice.entity.itr.ItrFlight;
import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.repository.itr.ItrFlightRepository;
import com.uraltrans.logisticparamservice.repository.postgres.FlightRepository;
import com.uraltrans.logisticparamservice.service.mapper.mapstruct.ItrFlightMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightService;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {
    private final FlightRepository flightRepository;
    private final ItrFlightRepository itrFlightRepository;
    private final ItrFlightMapper itrFlightMapper;

    @Override
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    @Override
    public List<LoadIdleDto> getGroupedCarLoadIdle() {
        return flightRepository.groupCarLoadIdle();
    }

    @Override
    public List<UnloadIdleDto> getGroupCarUnloadIdle() {
        return flightRepository.groupCarUnloadIdle();
    }

    @Override
    public void saveAllFlights(LoadParameters dto) {
        prepareNextSave();
        List<Flight> allFlights = getAllFlights(dto.getDaysToRetrieveData());
        logFlightErrors(allFlights);
        flightRepository.saveAll(allFlights);
    }

    @Override
    public void saveAll(List<Flight> flights) {
        flightRepository.saveAll(flights);
    }

    private List<Flight> getAllFlights(int days) {
        String from = LocalDate.now().minusDays(days).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String to = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<ItrFlight> itrFlights = itrFlightRepository.getAllFlightsFromItr(from, to);
        return itrFlightMapper.toFlightList(itrFlights);
    }

    private void prepareNextSave() {
        flightRepository.truncate();
    }

    private void logFlightErrors(List<Flight> allFlights) {
        List<String> errorFlights = new ArrayList<>();
        allFlights
                .forEach(f -> {
                    if(f.getCargo() == null){
                        errorFlights.add(f + " --- (груз не найден)");
                    }
                    else if(f.getSourceStationCode() == null){
                        errorFlights.add(f + " --- (станция отправления не найдена)");
                    }
                    else if(f.getDestStationCode() == null){
                        errorFlights.add(f + " --- (станция назначения не найдена)");
                    }
                    else if(f.getSourceStationCode() != null && f.getSourceStationCode().length() != 6){
                        errorFlights.add(f + " --- (некорректный код станции отправления)");
                    }
                    else if(f.getDestStationCode() != null && f.getDestStationCode().length() != 6){
                        errorFlights.add(f + " --- (некорректный код станции назначения)");
                    }
                    else if(f.getVolume() == null){
                        errorFlights.add(f + " --- (объем кузова не найден)" );
                    }
                });

        FileUtils.writeDiscardedFlights(errorFlights, false);
    }
}
