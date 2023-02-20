package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.FlightIdle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Optional;

public interface FlightIdleRepository extends JpaRepository<FlightIdle, String> {
    @Query("""
           SELECT AVG(f.loading) FROM FlightIdle f
           WHERE f.departureStationCode = :stationCode
           GROUP BY f.departureStationCode
           """)
    Optional<String> findLoadIdleByStationCode(String stationCode);

    @Query("""
           SELECT AVG(f.unloading) FROM FlightIdle f
           WHERE f.departureStationCode = :stationCode
           GROUP BY  f.departureStationCode
           """)
    Optional<String> findUnloadIdleByStationCode(String stationCode);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE flight_idles", nativeQuery = true)
    void truncate();

}
