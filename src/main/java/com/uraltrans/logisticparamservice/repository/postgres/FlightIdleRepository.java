package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.FlightIdle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Optional;

public interface FlightIdleRepository extends JpaRepository<FlightIdle, String> {
    @Query("select avg(f.loading) from FlightIdle f " +
           "where f.departureStationCode = :stationCode " +
           "group by f.departureStationCode")
    Optional<String> findLoadIdleByStationCode(String stationCode);

    @Query("select avg(f.unloading) from FlightIdle f " +
           "where f.departureStationCode = :stationCode " +
           "group by f.departureStationCode")
    Optional<String> findUnloadIdleByStationCode(String stationCode);

    @Modifying
    @Transactional
    @Query(value = "truncate table flight_idles", nativeQuery = true)
    void truncate();

}
