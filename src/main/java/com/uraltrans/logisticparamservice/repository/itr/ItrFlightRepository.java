package com.uraltrans.logisticparamservice.repository.itr;

import com.uraltrans.logisticparamservice.entity.itr.ItrFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItrFlightRepository extends JpaRepository<ItrFlight, Long> {

    @Query(value = "EXEC Dashboard.Flight_ :from, :to", nativeQuery = true)
    List<ItrFlight> getAllFlightsFromItr(String from, String to);
}
