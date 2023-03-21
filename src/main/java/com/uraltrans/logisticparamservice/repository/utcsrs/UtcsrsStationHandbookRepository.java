package com.uraltrans.logisticparamservice.repository.utcsrs;

import com.uraltrans.logisticparamservice.entity.utcsrs.UtcsrsStationHandbook;
import com.uraltrans.logisticparamservice.entity.utcsrs.projection.UtcsrsStationHandbookProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UtcsrsStationHandbookRepository extends JpaRepository<UtcsrsStationHandbook, Integer> {

    @Query(value = """
            SELECT r45._Code as Code, r45._Description as Description, r3349._Description as Region,
            r46._Description as Road, r45._Fld6624 as Latitude, r45._Fld6625 as Longitude,
            r45._Fld6790 as ExcludeFromSecondEmptyFlight, r45._Fld2543 as Lock
            FROM _Reference45 r45
            LEFT OUTER JOIN _Reference3349 r3349 ON r45._Fld3371RRef = r3349._IDRRef
            LEFT OUTER JOIN _Reference46 r46 ON r3349._Fld3355RRef = r46._IDRRef
        """, nativeQuery = true)
    List<UtcsrsStationHandbookProjection> getAllStations();
}
