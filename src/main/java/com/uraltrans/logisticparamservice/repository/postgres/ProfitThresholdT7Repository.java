package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.ProfitThresholdT7;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProfitThresholdT7Repository extends JpaRepository<ProfitThresholdT7, Integer> {
    @Query("SELECT p FROM ProfitThresholdT7 p WHERE :volume BETWEEN p.fromVolume AND p.toVolume")
    ProfitThresholdT7 findByVolume(Integer volume);
}
