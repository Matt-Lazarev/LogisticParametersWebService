package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.SecondEmptyFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface SecondEmptyFlightRepository extends JpaRepository<SecondEmptyFlight, Long> {

    @Query("select count(s) from SecondEmptyFlight s where s.carNumber = :carNumber")
    Integer countSecondEmptyFlightByCarNumber(Integer carNumber);

    @Modifying
    @Transactional
    @Query(value = "truncate table second_empty_flights restart identity", nativeQuery = true)
    void truncate();
}
