package com.uraltrans.logisticparamservice.mapper;

import com.uraltrans.logisticparamservice.dto.LoadingUnloadingDto;
import com.uraltrans.logisticparamservice.entity.postgres.LoadingUnloadingIdle;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
