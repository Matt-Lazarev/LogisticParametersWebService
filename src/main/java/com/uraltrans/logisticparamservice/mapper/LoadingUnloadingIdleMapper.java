package com.uraltrans.logisticparamservice.mapper;

import com.uraltrans.logisticparamservice.dto.idle.LoadIdleDto;
import com.uraltrans.logisticparamservice.dto.idle.LoadingUnloadingDto;
import com.uraltrans.logisticparamservice.dto.idle.UnloadIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.LoadingUnloadingIdle;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LoadingUnloadingIdleMapper {

    public List<LoadingUnloadingDto> mapToListDto(List<LoadingUnloadingIdle> idles){
        List<LoadingUnloadingDto> dtos = idles
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        Stream.iterate(1, i -> i + 1)
                .limit(dtos.size())
                .forEach(i -> dtos.get(i-1).setId(Integer.toString(i)));

        return dtos;
    }

    private LoadingUnloadingDto mapToDto(LoadingUnloadingIdle idle) {
        LoadingUnloadingDto dto = new LoadingUnloadingDto();
        dto.setDepartureStation(idle.getDepartureStation());
        dto.setCargo(idle.getCargo());
        dto.setWagonType(idle.getWagonType());
        dto.setVolume(idle.getVolume());
        dto.setLoading(idle.getLoading());
        dto.setUnloading(idle.getUnloading());
        return dto;
    }

    public List<LoadingUnloadingIdle> mapToLoadingUnloadingList(
            List<LoadIdleDto> loadIdleDtos, List<UnloadIdleDto> unloadIdleDtos) {
        List<LoadingUnloadingIdle> result = new ArrayList<>();
        for (int i = 0; i < loadIdleDtos.size(); i++) {
            LoadIdleDto load = loadIdleDtos.get(i);
            for (int j = 0; j < unloadIdleDtos.size(); j++) {
                UnloadIdleDto unload = unloadIdleDtos.get(j);
                if (Objects.equals(load.getSourceStationCode(), unload.getDestStationCode())
                        && Objects.equals(load.getVolume(), unload.getVolume())
                        && Objects.equals(load.getCargoCode6(), unload.getCargoCode6())
                        && Objects.equals(load.getCarType(), unload.getCarType())) {
                    LoadingUnloadingIdle dto = mapToLoadingUnloading(load, unload);
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

    private LoadingUnloadingIdle mapToLoadingUnloading(LoadIdleDto load, UnloadIdleDto unload) {
        if (load == null) {
            return new LoadingUnloadingIdle(
                    unload.getDestStation(), unload.getDestStationCode(),
                    unload.getCargoCode6(), unload.getCarType(), unload.getVolume(),
                    null, unload.getCarUnloadIdleDays());
        } else if (unload == null) {
            return new LoadingUnloadingIdle(
                    load.getSourceStation(), load.getSourceStationCode(),
                    load.getCargoCode6(), load.getCarType(), load.getVolume(),
                    load.getCarLoadIdleDays(), null);
        }

        return new LoadingUnloadingIdle(
                load.getSourceStation(), load.getSourceStationCode(),
                load.getCargoCode6(), load.getCarType(), load.getVolume(),
                load.getCarLoadIdleDays(), unload.getCarUnloadIdleDays());
    }
}
