package com.uraltrans.logisticparamservice.mapper;

import com.uraltrans.logisticparamservice.dto.LoadIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.LoadingUnloadingIdle;
import com.uraltrans.logisticparamservice.dto.UnloadIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FlightMapper {

    public List<Flight> mapRawFlightsDataToFlightsList(List<Map<String, Object>> flightsData) {
        return flightsData
                .stream()
                .map(this::mapToFlight)
                .collect(Collectors.toList());
    }

    private Flight mapToFlight(Map<String, Object> flightData) {
        return new Flight()
                .setAid((Integer) flightData.get("AID"))
                .setCarNumber((Integer) flightData.get("CarNumber"))
                .setInvNumber((String) flightData.get("InvNumber"))
                .setPrevInvNumber((String) flightData.get("PrevInvNumber"))
                .setNextInvNumber((String) flightData.get("NextInvNumber"))
                .setSendDate((Timestamp) flightData.get("SendDate"))
                .setSourceStation((String) flightData.get("SourceStation"))
                .setSourceStationCode((String) flightData.get("FVrh_3"))
                .setDestStation((String) flightData.get("DestStation"))
                .setDestStationCode((String) flightData.get("FVrh_4"))
                .setCargo((String) flightData.get("Cargo"))
                .setCargoCode6((String) flightData.get("CargoCode6"))
                .setCarType((String) flightData.get("CarType"))
                .setArriveToDestStationDate((Timestamp) flightData.get("DateInDate"))
                .setArriveToSourceStationDate((Timestamp) flightData.get("DateIn"))
                .setDepartureDate((Timestamp) flightData.get("DateOut"))
                .setVolume((BigDecimal) flightData.get("Volume"))
                .setLoaded((String) flightData.get("Loaded"))
                .setDepartureFromDestStationDate((Timestamp) flightData.get("Отправление со ст. назн."))
                .setUnloadOnDestStationDate((Timestamp) flightData.get("Выгрузка на ст. назн."))
                .setFlightKind((String) flightData.get("FlightKind"))
                .setNextFlightStartDate((Timestamp) flightData.get("Дата нач. след. Рейса (дата оформления вагона порожним)"));
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
