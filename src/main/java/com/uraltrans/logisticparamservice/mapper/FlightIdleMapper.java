package com.uraltrans.logisticparamservice.mapper;

import com.uraltrans.logisticparamservice.dto.idle.LoadIdleDto;
import com.uraltrans.logisticparamservice.dto.idle.FlightIdleDto;
import com.uraltrans.logisticparamservice.dto.idle.UnloadIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.FlightIdle;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FlightIdleMapper {

    public List<FlightIdleDto> mapToListDto(List<FlightIdle> idles){
        List<FlightIdleDto> dtos = idles
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        Stream.iterate(1, i -> i + 1)
                .limit(dtos.size())
                .forEach(i -> dtos.get(i-1).setId(Integer.toString(i)));

        return dtos;
    }

    private FlightIdleDto mapToDto(FlightIdle idle) {
        FlightIdleDto dto = new FlightIdleDto();
        dto.setDepartureStation(idle.getDepartureStation());
        dto.setCargo(idle.getCargo());
        dto.setWagonType(idle.getWagonType());
        dto.setVolume(idle.getVolume());
        dto.setLoading(idle.getLoading());
        dto.setUnloading(idle.getUnloading());
        return dto;
    }

    public List<FlightIdle> mapToLoadingUnloadingList(
            List<LoadIdleDto> loadIdleDtos, List<UnloadIdleDto> unloadIdleDtos) {
        List<FlightIdle> result = new ArrayList<>();
        for (int i = 0; i < loadIdleDtos.size(); i++) {
            LoadIdleDto load = loadIdleDtos.get(i);
            for (int j = 0; j < unloadIdleDtos.size(); j++) {
                UnloadIdleDto unload = unloadIdleDtos.get(j);
                if (Objects.equals(load.getSourceStationCode(), unload.getDestStationCode())
                        && Objects.equals(load.getVolume(), unload.getVolume())
                        && Objects.equals(load.getCargoCode6(), unload.getCargoCode6())
                        && Objects.equals(load.getCarType(), unload.getCarType())) {
                    FlightIdle dto = mapToLoadingUnloading(load, unload);
                    result.add(dto);

                    loadIdleDtos.remove(i);
                    unloadIdleDtos.remove(j);
                    i--;
                    j--;
                }
            }
        }
        loadIdleDtos.forEach(load -> result.add(mapToLoadingUnloading(load, null)));
        unloadIdleDtos.forEach(unload -> result.add(mapToLoadingUnloading(null, unload)));

        return result;
    }

    private FlightIdle mapToLoadingUnloading(LoadIdleDto load, UnloadIdleDto unload) {
        if (load == null) {
            return new FlightIdle(
                    unload.getDestStation(), unload.getDestStationCode(),
                    unload.getCargoCode6(), unload.getCarType(), unload.getVolume(),
                    null, unload.getCarUnloadIdleDays());
        } else if (unload == null) {
            return new FlightIdle(
                    load.getSourceStation(), load.getSourceStationCode(),
                    load.getCargoCode6(), load.getCarType(), load.getVolume(),
                    load.getCarLoadIdleDays(), null);
        }

        return new FlightIdle(
                load.getSourceStation(), load.getSourceStationCode(),
                load.getCargoCode6(), load.getCarType(), load.getVolume(),
                load.getCarLoadIdleDays(), unload.getCarUnloadIdleDays());
    }
}
