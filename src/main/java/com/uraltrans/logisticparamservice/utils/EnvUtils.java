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
        Integer daysToRetrieveData = Integer.parseInt(Objects.requireNonNull(env.getProperty("params.daysToRetrieveData")));
        String getNextDataLoadTime = Objects.requireNonNull(env.getProperty("params.nextDataLoadTime"));
        Integer maxLoadIdleDays = Integer.parseInt(Objects.requireNonNull(env.getProperty("params.maxLoadIdleDays")));
        Integer maxUnloadIdleDays = Integer.parseInt(Objects.requireNonNull(env.getProperty("params.maxUnloadIdleDays")));
        Integer minLoadIdleDays = Integer.parseInt(Objects.requireNonNull(env.getProperty("params.minLoadIdleDays")));
        Integer minUnloadIdleDays = Integer.parseInt(Objects.requireNonNull(env.getProperty("params.minUnloadIdleDays")));
        Double maxTravelTime = Double.parseDouble(Objects.requireNonNull(env.getProperty("params.maxTravelTime")));
        Double minTravelTime = Double.parseDouble(Objects.requireNonNull(env.getProperty("params.minTravelTime")));

        return new LoadDataRequestDto(daysToRetrieveData, getNextDataLoadTime, maxLoadIdleDays, maxUnloadIdleDays,
                minLoadIdleDays, minUnloadIdleDays, maxTravelTime, minTravelTime); //FIXME accessors chain
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
        map.put("params.minTravelTime", dto.getMaxTravelTime());

        propertySources.addFirst(new MapPropertySource("newmap", map));
    }

    public static LocalTime getNextDataLoadTime(Environment env) {
        String time = env.getProperty("params.nextDataLoadTime");
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
