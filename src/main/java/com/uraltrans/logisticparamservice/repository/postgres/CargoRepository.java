package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface CargoRepository extends JpaRepository<Cargo, Long> {

    @Query("select c.name from Cargo c where c.code = :code")
    String findCargoNameByCode(String code);

    @Modifying
    @Transactional
    @Query(value = "truncate table cargos restart identity", nativeQuery = true)
    void truncate();
}
