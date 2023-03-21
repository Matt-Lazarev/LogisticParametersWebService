package com.uraltrans.logisticparamservice.service.mapper.mapstruct;

import com.uraltrans.logisticparamservice.dto.station.StationResponse;
import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;
import com.uraltrans.logisticparamservice.entity.utcsrs.projection.UtcsrsStationHandbookProjection;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UtcsrsStationHandbookMapper {

    @Mapping(target = "code6", source = "utcsrsStationHandbook.code")
    @Mapping(target = "station", source = "utcsrsStationHandbook.description")
    @Mapping(target = "excludeFromSecondEmptyFlight", ignore = true)
    @Mapping(target = "lock", ignore = true)
    StationHandbook toStationHandbook(UtcsrsStationHandbookProjection utcsrsStationHandbook);

    List<StationHandbook> toStationHandbookList(List<UtcsrsStationHandbookProjection> utcsrsStationHandbookList);

    @Mapping(target = "idStation", source = "stationHandbook.code6")
    @Mapping(target = "nameStation", source = "stationHandbook.station")
    @Mapping(target = "way", source = "stationHandbook.road")
    StationResponse toStationResponse(StationHandbook stationHandbook);

    List<StationResponse> toStationResponseList(List<StationHandbook> stationHandbooks);

    @BeforeMapping
    default void stationHandbookPreConstruct(UtcsrsStationHandbookProjection utcsrsStationHandbook,
                                             @MappingTarget StationHandbook stationHandbook){
        stationHandbook.setCode5(utcsrsStationHandbook.getCode().length() == 6
                ? utcsrsStationHandbook.getCode().substring(0, 5)
                : utcsrsStationHandbook.getCode());
        stationHandbook.setExcludeFromSecondEmptyFlight(utcsrsStationHandbook.getExcludeFromSecondEmptyFlight().equals("1"));
        stationHandbook.setLock(utcsrsStationHandbook.getLock().equals("1"));
    }

    @AfterMapping
    default void stationResponseListPostConstruct(@MappingTarget List<StationResponse> stationResponses){
        stationResponses.forEach(sr ->{
                sr.setSuccess("true");
                sr.setErrorText("");
        });
    }
}
