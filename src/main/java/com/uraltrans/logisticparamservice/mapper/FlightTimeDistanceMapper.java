package com.uraltrans.logisticparamservice.mapper;

import com.uraltrans.logisticparamservice.entity.postgres.FlightTimeDistance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FlightTimeDistanceMapper {

    public List<FlightTimeDistance> mapToList(Map<String, String> distLoaded, Map<String, String> distUnloaded,
                                        Map<String, String> timeLoaded, Map<String, String> timeUnloaded) {
        List<FlightTimeDistance> loaded = mapFlightType(distLoaded, timeLoaded, "ГРУЖ");
        List<FlightTimeDistance> unloaded = mapFlightType(distUnloaded, timeUnloaded, "ПОР");
        return Stream.concat(loaded.stream(), unloaded.stream()).collect(Collectors.toList());
    }

    private List<FlightTimeDistance> mapFlightType(Map<String, String> dist, Map<String, String> time, String type){
        List<FlightTimeDistance> result = new ArrayList<>();
        for(Map.Entry<String, String> entry : dist.entrySet()){
            String[] stationCodes = entry.getKey().split(" ");
            FlightTimeDistance timeDistance = new FlightTimeDistance();

            timeDistance.setFlightType(type);
            timeDistance.setDepartureStationCode(stationCodes[0]);
            timeDistance.setDestinationStationCode(stationCodes[1]);
            timeDistance.setDistance(entry.getValue());
            timeDistance.setTravelTime(time.get(entry.getKey()));
            result.add(timeDistance);
        }
        return result;
    }
}
