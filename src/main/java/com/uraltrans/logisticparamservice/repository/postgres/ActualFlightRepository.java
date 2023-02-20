package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.ActualFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigDecimal;

public interface ActualFlightRepository extends JpaRepository<ActualFlight, Long> {

    @Query("""
           SELECT co
           FROM ActualFlight co
           WHERE co.sourceStationCode = :sourceStation AND
           co.destinationStationCode = :destStation AND
           co.volume = :volume
           """)
    ActualFlight findByStationCodesAndVolume(String sourceStation, String destStation, BigDecimal volume);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE actual_flights RESTART IDENTITY", nativeQuery = true)
    void truncate();
}
