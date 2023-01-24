package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.TariffResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffResponseRepository extends JpaRepository<TariffResponse, String> { }
