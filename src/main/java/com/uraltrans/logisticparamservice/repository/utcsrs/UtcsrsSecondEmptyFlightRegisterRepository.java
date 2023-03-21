package com.uraltrans.logisticparamservice.repository.utcsrs;

import com.uraltrans.logisticparamservice.entity.utcsrs.UtcsrsSecondEmptyFlightRegister;
import com.uraltrans.logisticparamservice.entity.utcsrs.projection.UtcsrsSecondEmptyFlightRegisterProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UtcsrsSecondEmptyFlightRegisterRepository extends JpaRepository<UtcsrsSecondEmptyFlightRegister, Integer> {
    @Query(value = """
            SELECT r45_1._Code as SourceStation, r45_2._Code as DestStation
            FROM _InfoRg6810 inf
            LEFT OUTER JOIN _Reference45 r45_1 on inf._Fld6811RRef = r45_1._IDRRef
            LEFT OUTER JOIN _Reference45 r45_2 on inf._Fld6812RRef = r45_2._IDRRef
            WHERE r45_1._Code = :sourceStation AND r45_2._Code = :destStation
        """, nativeQuery = true)
    List<UtcsrsSecondEmptyFlightRegisterProjection> getAllRegisterRowsByStations(String sourceStation, String destStation);
}
