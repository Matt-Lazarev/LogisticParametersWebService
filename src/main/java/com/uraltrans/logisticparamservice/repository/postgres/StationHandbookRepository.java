package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface StationHandbookRepository extends JpaRepository<StationHandbook, String> {

    @Query("SELECT s.region FROM StationHandbook s WHERE s.code6 = :code")
    String findRegionByCode6(String code);

    @Query("SELECT s FROM StationHandbook s WHERE s.code6 = :code")
    StationHandbook findStationByCode6(String code);

    @Query("SELECT s.region FROM StationHandbook  s WHERE s.station = :station")
    String findRegionByStation(String station);

    @Query("SELECT s FROM StationHandbook s WHERE s.station = :station")
    List<StationHandbook> findStationByName(String station);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE station_handbook RESTART IDENTITY", nativeQuery = true)
    void truncate();


}
