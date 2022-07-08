package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.dto.UnloadIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.dto.LoadIdleDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("select new com.intertrastcargo.stationlogisticparamservice.dto.LoadIdleDto " +
           "(f.volume, f.cargoCode6, f.sourceStation, f.sourceStationCode, f.carType, AVG(f.carLoadIdleDays)) " +
           "from Flight f " +
           "group by f.volume, f.cargoCode6, f.sourceStation, f.sourceStationCode, f.carType")
    List<LoadIdleDto> groupCarLoadIdle();

    @Query("select new com.intertrastcargo.stationlogisticparamservice.dto.UnloadIdleDto " +
            "(f.volume, f.cargoCode6, f.destStation, f.destStationCode, f.carType, AVG(f.carUnloadIdleDays)) " +
            "from Flight f " +
            "group by f.volume, f.cargoCode6, f.destStation, f.destStationCode, f.carType")
    List<UnloadIdleDto> groupCarUnloadIdle();
}
