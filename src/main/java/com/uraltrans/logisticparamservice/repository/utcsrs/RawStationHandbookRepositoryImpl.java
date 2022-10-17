package com.uraltrans.logisticparamservice.repository.utcsrs;

import com.uraltrans.logisticparamservice.utils.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;

@Repository
public class RawStationHandbookRepositoryImpl implements RawStationHandbookRepository {
    private static final String SQL =
            "SELECT r45._Code, r45._Description, r3349._Description as _Region, " +
            "r46._Description as _Road, r45._Fld6624 as _Latitude, r45._Fld6625 as _Longitude, " +
            "r45._Fld6790 as _ExcludeFromSecondEmptyFlight, r45._Fld2543 as _Lock " +
            "FROM _Reference45 r45 " +
            "LEFT OUTER JOIN _Reference3349 r3349 ON r45._Fld3371RRef = r3349._IDRRef " +
            "LEFT OUTER JOIN _Reference46 r46 ON r3349._Fld3355RRef = r46._IDRRef " +
            "WHERE r45._Marked != 'AQ=='";

    @Resource(name = "utcsrsDataSource")
    private final DataSource utcsrsDataSource;

    public RawStationHandbookRepositoryImpl(DataSource utcsrsDataSource) {
        this.utcsrsDataSource = utcsrsDataSource;
    }

    @Override
    public List<Map<String, Object>> getAllStations() {
        return JdbcUtils.getAllData(utcsrsDataSource, SQL);
    }
}
