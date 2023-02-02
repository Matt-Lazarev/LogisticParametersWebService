package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationAnalysisT15;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RegionSegmentationAnalysisT15Repository extends JpaRepository<RegionSegmentationAnalysisT15, Integer> {
    @Modifying
    @Transactional
    @Query(value = "truncate table t15_analyzed restart identity", nativeQuery = true)
    void truncate();
}
