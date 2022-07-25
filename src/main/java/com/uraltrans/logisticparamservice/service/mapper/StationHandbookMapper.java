package com.uraltrans.logisticparamservice.service.mapper;

import com.uraltrans.logisticparamservice.dto.station.StationResponse;
import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StationHandbookMapper {

    public List<StationHandbook> mapRawStationHandbookData(List<Map<String, Object>> data) {
        return data
                .stream()
                .map(this::mapToStationHandbook)
                .collect(Collectors.toList());
    }

    private StationHandbook mapToStationHandbook(Map<String, Object> stationData) {
        return new StationHandbook()
                .setCode6((String) stationData.get("_Code"))
                .setCode5(((String) stationData.get("_Code")).substring(0, 5))
                .setStation((String) stationData.get("_Description"))
                .setRegion((String) stationData.get("_Region"))
                .setRoad((String) stationData.get("_Road"))
                .setLatitude((String) stationData.get("_Latitude"))
                .setLongitude((String) stationData.get("_Longitude"));
    }

    public List<StationResponse> mapToListResponses(List<StationHandbook> stationHandbooks) {
        return stationHandbooks
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private StationResponse mapToResponse(StationHandbook stationHandbook) {
        return StationResponse.builder()
                .success("true")
                .errorText("")
                .idStation(stationHandbook.getCode6())
                .nameStation(stationHandbook.getStation())
                .region(stationHandbook.getRegion())
                .way(stationHandbook.getRoad())
                .latitude(stationHandbook.getLatitude())
                .longitude(stationHandbook.getLongitude())
                .build();
    }
}
