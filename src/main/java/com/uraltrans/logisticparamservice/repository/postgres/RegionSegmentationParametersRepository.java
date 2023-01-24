package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationParameters;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionSegmentationParametersRepository extends JpaRepository<RegionSegmentationParameters, Integer> {
}
