package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.ActualFlight;
import com.uraltrans.logisticparamservice.entity.postgres.ClientOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigDecimal;

public interface ActualFlightRepository extends JpaRepository<ActualFlight, Long> {

    @Query("select co " +
            "from ActualFlight co " +
            "where co.sourceStationCode = :sourceStation and " +
            "      co.destinationStationCode = :destStation and " +
            "      co.volume = :volume")
    ActualFlight findByStationCodesAndVolume(String sourceStation, String destStation, BigDecimal volume);

    @Modifying
    @Transactional
    @Query(value = "truncate table actual_flights restart identity", nativeQuery = true)
    void truncate();
}
