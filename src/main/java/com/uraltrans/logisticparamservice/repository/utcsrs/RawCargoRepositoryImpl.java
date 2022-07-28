package com.uraltrans.logisticparamservice.repository.utcsrs;

import com.uraltrans.logisticparamservice.utils.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class RawCargoRepositoryImpl implements RawCargoRepository {
    private static final String SQL =
            "SELECT DISTINCT r35._Code as _Code, r35._Fld2401 as _CargoName " +
            "FROM _Reference33 r33 " +
            "LEFT OUTER JOIN _Reference35 r35 on r33._Fld2396RREF = r35._IDRRef";

    @Resource(name = "utcsrsDataSource")
    private final DataSource utcsrsDataSource;

    public RawCargoRepositoryImpl(DataSource utcsrsDataSource) {
        this.utcsrsDataSource = utcsrsDataSource;
    }

    @Override
    public List<Map<String, Object>> getAllFlightProfits() {
        try {
            Connection connection = utcsrsDataSource.getConnection();
            return JdbcUtils.getAllData(connection, SQL);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
