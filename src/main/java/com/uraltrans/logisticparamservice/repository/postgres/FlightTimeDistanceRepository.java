package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.FlightTimeDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface FlightTimeDistanceRepository extends JpaRepository<FlightTimeDistance, Long> {

    @Query("select f from FlightTimeDistance f " +
           "where f.departureStationCode = :departureStation " +
           "and f.destinationStationCode = :destinationStationCode " +
           "and f.flightType = :flightType")
    FlightTimeDistance findByStationCodesAndFlightType(String departureStation, String destinationStation, String flightType);

    @Modifying
    @Transactional
    @Query(value = "truncate table flight_times_distances restart identity", nativeQuery = true)
    void truncate();
}
