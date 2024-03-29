package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.dto.idle.LoadUnloadIdleDto;
import com.uraltrans.logisticparamservice.dto.idle.UnloadIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.dto.idle.LoadIdleDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("""
           SELECT new com.uraltrans.logisticparamservice.dto.idle.LoadIdleDto
               (f.volume, f.cargo, f.cargoCode6, f.sourceStation, f.sourceStationCode, f.carType, AVG(f.carLoadIdleDays))
           FROM Flight f
           WHERE upper(f.loaded) = 'ГРУЖ' AND upper(f.carType) = 'КР'
           GROUP BY  f.volume, f.cargo, f.cargoCode6, f.sourceStation, f.sourceStationCode, f.carType
           """)
    List<LoadIdleDto> groupCarLoadIdle();

    @Query("""
           SELECT new com.uraltrans.logisticparamservice.dto.idle.UnloadIdleDto
               (f.volume, f.cargo, f.cargoCode6, f.destStation, f.destStationCode, f.carType, AVG(f.carUnloadIdleDays))
           FROM Flight f
           WHERE upper(f.loaded) = 'ГРУЖ' AND upper(f.carType) = 'КР'
           GROUP BY f.volume, f.cargo, f.cargoCode6, f.destStation, f.destStationCode, f.carType
           """)
    List<UnloadIdleDto> groupCarUnloadIdle();

    @Query("""
           SELECT new com.uraltrans.logisticparamservice.dto.idle.LoadUnloadIdleDto
               ('', '', f.sourceStation, f.sourceStationCode, f.destStation, f.destStationCode, AVG(f.carLoadIdleDays), AVG(f.carUnloadIdleDays))
           FROM Flight f
           WHERE upper(f.loaded) = 'ГРУЖ' AND upper(f.carType) = 'КР'
           GROUP BY f.sourceStation, f.sourceStationCode, f.destStation, f.destStationCode
           """)
    List<LoadUnloadIdleDto> groupCarLoadUnloadIdle();

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE flights RESTART IDENTITY", nativeQuery = true)
    void truncate();
}
