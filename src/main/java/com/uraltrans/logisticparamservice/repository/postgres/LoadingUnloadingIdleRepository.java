package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.LoadingUnloadingIdle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoadingUnloadingIdleRepository extends JpaRepository<LoadingUnloadingIdle, String> {
}
