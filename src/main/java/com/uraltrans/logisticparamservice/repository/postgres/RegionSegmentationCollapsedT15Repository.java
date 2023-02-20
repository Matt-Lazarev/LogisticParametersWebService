package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationCollapsedT15;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RegionSegmentationCollapsedT15Repository extends JpaRepository<RegionSegmentationCollapsedT15, Integer> {
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE t15_collapsed RESTART IDENTITY", nativeQuery = true)
    void truncate();
}
