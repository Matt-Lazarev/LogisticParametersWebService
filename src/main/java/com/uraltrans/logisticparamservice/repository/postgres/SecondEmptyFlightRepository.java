package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.SecondEmptyFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface SecondEmptyFlightRepository extends JpaRepository<SecondEmptyFlight, Long> {

    @Query("SELECT COUNT(s) FROM SecondEmptyFlight s WHERE s.carNumber = :carNumber")
    Integer countSecondEmptyFlightByCarNumber(Integer carNumber);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE second_empty_flights RESTART IDENTITY", nativeQuery = true)
    void truncate();
}
