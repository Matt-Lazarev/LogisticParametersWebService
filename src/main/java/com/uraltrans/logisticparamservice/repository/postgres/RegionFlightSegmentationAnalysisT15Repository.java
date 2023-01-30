package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.RegionFlightSegmentationAnalysisT15;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RegionFlightSegmentationAnalysisT15Repository extends JpaRepository<RegionFlightSegmentationAnalysisT15, Integer> {
    @Modifying
    @Transactional
    @Query(value = "truncate table t15_analyzed restart identity", nativeQuery = true)
    void truncate();
}
