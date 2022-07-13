package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.FlightIdle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface FlightIdleRepository extends JpaRepository<FlightIdle, String> {
    @Modifying
    @Transactional
    @Query(value = "truncate table flight_idles restart identity", nativeQuery = true)
    void truncate();
}
