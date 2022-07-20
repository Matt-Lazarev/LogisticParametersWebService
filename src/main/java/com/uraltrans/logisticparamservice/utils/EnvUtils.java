package com.uraltrans.logisticparamservice.utils;

import com.uraltrans.logisticparamservice.dto.request.LoadDataRequestDto;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EnvUtils {
    public static LoadDataRequestDto getRequestParams(Environment env) {
        Integer daysToRetrieveData = env.getProperty("params.daysToRetrieveData", Integer.class);
        String getNextDataLoadTime = env.getProperty("params.nextDataLoadTime");
        Integer maxLoadIdleDays = env.getProperty("params.maxLoadIdleDays", Integer.class);
        Integer maxUnloadIdleDays = env.getProperty("params.maxUnloadIdleDays", Integer.class);
        Integer minLoadIdleDays = env.getProperty("params.minLoadIdleDays", Integer.class);
        Integer minUnloadIdleDays = env.getProperty("params.minUnloadIdleDays", Integer.class);
        Double maxTravelTime = env.getProperty("params.maxTravelTime", Double.class);
        Double minTravelTime = env.getProperty("params.minTravelTime", Double.class);
        Integer flightProfitDaysToRetrieveData = env.getProperty("params.flightProfitDaysToRetrieveData", Integer.class);

        return new LoadDataRequestDto(daysToRetrieveData, getNextDataLoadTime, maxLoadIdleDays, maxUnloadIdleDays,
                minLoadIdleDays, minUnloadIdleDays, maxTravelTime, minTravelTime, flightProfitDaysToRetrieveData);
    }

    public static void updateEnvironment(ConfigurableEnvironment env, LoadDataRequestDto dto) {
        MutablePropertySources propertySources = env.getPropertySources();
        Map<String, Object> map = new HashMap<>();

        map.put("params.daysToRetrieveData", dto.getDaysToRetrieveData());
        map.put("params.nextDataLoadTime", dto.getNextDataLoadTime());
        map.put("params.maxLoadIdleDays", dto.getMaxLoadIdleDays());
        map.put("params.maxUnloadIdleDays", dto.getMaxUnloadIdleDays());
        map.put("params.minLoadIdleDays", dto.getMinLoadIdleDays());
        map.put("params.minUnloadIdleDays",dto.getMinLoadIdleDays());
        map.put("params.maxTravelTime", dto.getMaxTravelTime());
        map.put("params.minTravelTime", dto.getMinTravelTime());
        map.put("params.flightProfitDaysToRetrieveData", dto.getFlightProfitDaysToRetrieveData());

        propertySources.addFirst(new MapPropertySource("newmap", map));
    }

    public static LocalTime getNextDataLoadTime(Environment env) {
        String time = env.getProperty("params.nextDataLoadTime");
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
