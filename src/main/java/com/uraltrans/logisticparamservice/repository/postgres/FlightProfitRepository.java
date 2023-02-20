package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.FlightProfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface FlightProfitRepository extends JpaRepository<FlightProfit, Long> {
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE flight_profits RESTART IDENTITY", nativeQuery = true)
    void truncate();
}
