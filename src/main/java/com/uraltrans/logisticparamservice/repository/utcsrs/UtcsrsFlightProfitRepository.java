package com.uraltrans.logisticparamservice.repository.utcsrs;

import com.uraltrans.logisticparamservice.entity.utcsrs.UtcsrsFlightProfit;
import com.uraltrans.logisticparamservice.entity.utcsrs.projection.UtcstsFlightProfitProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UtcsrsFlightProfitRepository extends JpaRepository<UtcsrsFlightProfit, Integer> {

    @Query(value = """
        SELECT r45_1._Code as SourceStationCode, r45_2._Code as DestStationCode,
               r33._Code as CargoCode, r33._Fld5637 as Cargo,
               inf._Fld6441 as Profit, r22._Code as Currency,
               inf._Fld6629 as Volume, inf._Fld6626 as SendDate
        FROM _InfoRg5865 inf
        LEFT OUTER JOIN _Reference45 r45_1 on inf._Fld6583RRef = r45_1._IDRRef
        LEFT OUTER JOIN _Reference45 r45_2 on inf._Fld6584RRef = r45_2._IDRRef
        LEFT OUTER JOIN _Reference33 r33 on inf._Fld6585RRef = r33._IDRRef
        LEFT OUTER JOIN _Reference22 r22 on inf._Fld6578RRef = r22._IDRRef
        WHERE inf._Fld6626 >= :fromDate
        """, nativeQuery = true)
    List<UtcstsFlightProfitProjection> getAllFlightProfits(String fromDate);
}
