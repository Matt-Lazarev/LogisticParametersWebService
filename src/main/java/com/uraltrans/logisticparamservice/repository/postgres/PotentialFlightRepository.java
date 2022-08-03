package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.PotentialFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface PotentialFlightRepository extends JpaRepository<PotentialFlight, Long> {

    @Modifying
    @Transactional
    @Query(value = "truncate table potential_flights restart identity", nativeQuery = true)
    void truncate();
}
