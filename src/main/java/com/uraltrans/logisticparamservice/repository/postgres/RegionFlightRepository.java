package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.RegionFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RegionFlightRepository extends JpaRepository<RegionFlight, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE RegionFlight rf SET rf.travelDays = :travelDays WHERE rf.id = :id")
    void updateTravelTimeById(Integer travelDays, Integer id);
}
