package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RegionSegmentationLogRepository extends JpaRepository<RegionSegmentationLog, String> { }
