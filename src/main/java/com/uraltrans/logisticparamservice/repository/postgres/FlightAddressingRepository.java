package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.FlightAddressing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface FlightAddressingRepository extends JpaRepository<FlightAddressing, Long> {

    @Modifying
    @Transactional
    @Query(value = "truncate table flight_addressings restart identity", nativeQuery = true)
    void truncate();
}
