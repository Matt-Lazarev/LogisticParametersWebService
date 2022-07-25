package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.dto.idle.UnloadIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.dto.idle.LoadIdleDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("select new com.uraltrans.logisticparamservice.dto.idle.LoadIdleDto " +
           "(f.volume, f.cargo, f.cargoCode6, f.sourceStation, f.sourceStationCode, f.carType, AVG(f.carLoadIdleDays)) " +
           "from Flight f " +
           "where upper(f.loaded) = 'ГРУЖ' and upper(f.carType) = 'КР' " +
           "group by f.volume, f.cargo, f.cargoCode6, f.sourceStation, f.sourceStationCode, f.carType")
    List<LoadIdleDto> groupCarLoadIdle();

    @Query("select new com.uraltrans.logisticparamservice.dto.idle.UnloadIdleDto " +
            "(f.volume, f.cargo, f.cargoCode6, f.destStation, f.destStationCode, f.carType, AVG(f.carUnloadIdleDays)) " +
            "from Flight f " +
            "where upper(f.loaded) = 'ГРУЖ' and upper(f.carType) = 'КР' " +
            "group by f.volume, f.cargo, f.cargoCode6, f.destStation, f.destStationCode, f.carType")
    List<UnloadIdleDto> groupCarUnloadIdle();

    @Modifying
    @Transactional
    @Query(value = "truncate table flights restart identity", nativeQuery = true)
    void truncate();
}
