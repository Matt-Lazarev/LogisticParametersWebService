package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.NoDetailsWagon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface NoDetailsWagonRepository extends JpaRepository<NoDetailsWagon, Integer> {
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE no_details_wagons RESTART IDENTITY", nativeQuery = true)
    void truncate();
}
