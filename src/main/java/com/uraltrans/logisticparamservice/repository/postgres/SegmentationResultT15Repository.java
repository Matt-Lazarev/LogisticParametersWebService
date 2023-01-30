package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.SegmentationResultT15;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SegmentationResultT15Repository extends JpaRepository<SegmentationResultT15, Integer> {
    @Modifying
    @Transactional
    @Query(value = "truncate table t15_result restart identity", nativeQuery = true)
    void truncate();
}
