package com.uraltrans.logisticparamservice.repository.utcsrs;

import com.uraltrans.logisticparamservice.entity.utcsrs.UtcsrsCargo;
import com.uraltrans.logisticparamservice.entity.utcsrs.projection.UtcsrsCargoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UtcsrsCargoRepository extends JpaRepository<UtcsrsCargo, byte[]> {

    @Query(value = """
           SELECT DISTINCT r35._Code AS code, r35._Fld2401 AS name
           FROM _Reference33 r33
           LEFT OUTER JOIN _Reference35 r35
           ON r33._Fld2396RREF = r35._IDRRef
           """, nativeQuery = true)
    List<UtcsrsCargoProjection> getAllCargos();
}
