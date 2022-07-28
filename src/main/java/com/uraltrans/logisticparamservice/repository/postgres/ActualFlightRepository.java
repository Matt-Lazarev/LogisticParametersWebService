package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.ActualFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface ActualFlightRepository extends JpaRepository<ActualFlight, Long> {

    @Modifying
    @Transactional
    @Query(value = "truncate table actual_flights restart identity", nativeQuery = true)
    void truncate();
}
