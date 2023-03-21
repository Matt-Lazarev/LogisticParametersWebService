package com.uraltrans.logisticparamservice.repository.integration;

import com.uraltrans.logisticparamservice.entity.integration.IntegrationCarRepair;
import com.uraltrans.logisticparamservice.entity.integration.projection.IntegrationCarRepairProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IntegrationCarRepairRepository extends JpaRepository<IntegrationCarRepair, Integer> {

    @Query(
           value = """
               SELECT _Code as CarNumber, _Fld2701 as NonworkingPark,
               _Fld2702 as Refurbished, _Fld2703 as Rejected
               FROM _InfoRg2696 i2696
               INNER JOIN _Reference134 r134
               ON r134._IDRRef =  i2696._Fld2697RRef
               WHERE i2696._Fld2698 = :currentDate
           """, nativeQuery = true)
    List<IntegrationCarRepairProjection> getAllCarRepairs(String currentDate);


    @Query(
            value = """
                SELECT _Code as CarNumber,
                _Fld2701 as NonworkingPark,
                _Fld2702 as Refurbished,
                _Fld2703 as Rejected,
                _Fld2747 as RequiresRepair
                FROM _InfoRg2696 i2696
                INNER JOIN _Reference134 r134
                ON r134._IDRRef =  i2696._Fld2697RRef
                WHERE i2696._Fld2698 = :currentDate and _Code = :carNumber
            """, nativeQuery = true)
    Optional<IntegrationCarRepairProjection> getCarRepairByCarNumber(String currentDate, String carNumber);


    @Query(
            value = """
                SELECT _Code as CarNumber,
                _Fld284 as ThicknessWheel,
                _Fld285 as ThicknessComb
                FROM _InfoRg281 i281
                INNER JOIN _Reference134 r134
                ON r134._IDRRef = i281._Fld282RRef
                WHERE i281._Fld283 >= :currentDate
            """, nativeQuery = true)
    List<IntegrationCarRepairProjection> getAllCarWheelThicknesses(String currentDate);
}
