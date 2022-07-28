package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoadParametersRepository extends JpaRepository<LoadParameters, Long> {
}
