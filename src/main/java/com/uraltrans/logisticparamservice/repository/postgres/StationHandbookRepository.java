package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface StationHandbookRepository extends JpaRepository<StationHandbook, String> {

    @Query("select s.region from StationHandbook s where s.code6 = :code")
    String findRegionByCode6(String code);

    @Modifying
    @Transactional
    @Query(value = "truncate table station_handbook restart identity", nativeQuery = true)
    void truncate();
}
