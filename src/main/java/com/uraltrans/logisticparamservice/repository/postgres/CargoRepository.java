package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface CargoRepository extends JpaRepository<Cargo, Long> {

    @Query("SELECT c.name FROM Cargo c WHERE c.code = :code")
    String findCargoNameByCode(String code);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE cargos RESTART IDENTITY", nativeQuery = true)
    void truncate();
}
