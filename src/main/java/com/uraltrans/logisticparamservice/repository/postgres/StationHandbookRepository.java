package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface StationHandbookRepository extends JpaRepository<StationHandbook, String> {

    @Query("select s.region from StationHandbook s where s.code6 = :code")
    String findRegionByCode6(String code);

    @Query("select s from StationHandbook s where s.code6 = :code")
    StationHandbook findStationByCode6(String code);

    @Query("select s.region from StationHandbook  s where s.station = :station")
    String findRegionByStation(String station);

    @Query("select s from StationHandbook s where s.station = :station")
    List<StationHandbook> findStationByName(String station);

    @Modifying
    @Transactional
    @Query(value = "truncate table station_handbook restart identity", nativeQuery = true)
    void truncate();


}
