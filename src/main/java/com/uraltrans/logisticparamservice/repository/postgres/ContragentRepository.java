package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.Contragent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ContragentRepository extends JpaRepository<Contragent, Integer> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE contragents RESTART IDENTITY", nativeQuery = true)
    void truncate();
}
