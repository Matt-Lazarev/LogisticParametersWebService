package com.uraltrans.logisticparamservice.service.mapper;

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
        return idles
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private FlightIdleDto mapToDto(FlightIdle idle) {
        FlightIdleDto dto = new FlightIdleDto();
        dto.setId(Long.toString(idle.getId()));
        dto.setDepartureStation(idle.getDepartureStation());
        dto.setDepartureStationCode(idle.getDepartureStationCode());
        dto.setCargo(idle.getCargo());
        dto.setCargoCode(idle.getCargoCode());
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
            return FlightIdle.builder()
                    .departureStation(unload.getDestStation())
                    .departureStationCode(unload.getDestStationCode())
                    .cargo(unload.getCargo())
                    .cargoCode(unload.getCargoCode6())
                    .wagonType(unload.getCarType())
                    .volume(unload.getVolume() != null ? unload.getVolume().toString() : null)
                    .loading(null)
                    .unloading(unload.getCarUnloadIdleDays())
                    .build();
        }
        if (unload == null) {
            return FlightIdle.builder()
                    .departureStation(load.getSourceStation())
                    .departureStationCode(load.getSourceStationCode())
                    .cargo(load.getCargo())
                    .cargoCode(load.getCargoCode6())
                    .wagonType(load.getCarType())
                    .volume(load.getVolume() != null ? load.getVolume().toString() : null)
                    .loading(load.getCarLoadIdleDays())
                    .unloading(null)
                    .build();
        }
        return FlightIdle.builder()
                .departureStation(load.getSourceStation())
                .departureStationCode(load.getSourceStationCode())
                .cargo(load.getCargo())
                .cargoCode(load.getCargoCode6())
                .wagonType(load.getCarType())
                .volume(load.getVolume() != null ? load.getVolume().toString() : null)
                .loading(load.getCarLoadIdleDays())
                .unloading(unload.getCarUnloadIdleDays())
                .build();
    }
}
