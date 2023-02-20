package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.FlightTimeDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Optional;

public interface FlightTimeDistanceRepository extends JpaRepository<FlightTimeDistance, Long> {

    @Query("""
           SELECT f FROM FlightTimeDistance f
           WHERE f.departureStationCode = :departureStation AND
                 f.destinationStationCode = :destinationStation AND
                 f.typeFlight = :flightType
           """)
    Optional<FlightTimeDistance> findByStationCodesAndFlightType(String departureStation, String destinationStation, String flightType);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE flight_times_distances RESTART IDENTITY", nativeQuery = true)
    void truncate();
}
