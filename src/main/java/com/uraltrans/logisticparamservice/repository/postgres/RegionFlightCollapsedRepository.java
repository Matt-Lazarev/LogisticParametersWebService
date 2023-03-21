package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.RegionFlightCollapsed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RegionFlightCollapsedRepository extends JpaRepository<RegionFlightCollapsed, Integer> {
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE t15_collapsed RESTART IDENTITY", nativeQuery = true)
    void truncate();
}
