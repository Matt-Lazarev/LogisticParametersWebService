package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.Geocode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

public interface GeocodeRepository extends JpaRepository<Geocode, Long> {

    @Query("SELECT g.id FROM Geocode g WHERE g.stationCode = :stationCode")
    Long findGeocodeIdByStationCode(String stationCode);

    @Modifying
    @Transactional
    @Query("DELETE FROM Geocode g WHERE g.expiredAt <= :now")
    void deleteWhereExpired(LocalDateTime now);
}
