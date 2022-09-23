package com.uraltrans.logisticparamservice.repository.integration;

import com.uraltrans.logisticparamservice.utils.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class CarRepairInfoRepositoryImpl implements CarRepairInfoRepository {
    private static final String SQL1 =
                    "SELECT _Code as CarNumber, _Fld2701 as NonworkingPark, " +
                    "       _Fld2702 as Refurbished, _Fld2703 as Rejected " +
                    "FROM _InfoRg2696 i2696 " +
                    "INNER JOIN _Reference134 r134 " +
                    "ON r134._IDRRef =  i2696._Fld2697RRef " +
                    "WHERE i2696._Fld2698 >= ?";

    private static final String SQL2 =
                    "SELECT _Code as CarNumber, _Fld284 as ThicknessWheel " +
                    "FROM _InfoRg281 i281 " +
                    "INNER JOIN _Reference134 r134 " +
                    "ON r134._IDRRef = i281._Fld282RRef " +
                    "WHERE i281._Fld283 >= ?";

    @Resource(name = "integrationDataDataSource")
    private final DataSource integrationDataDataSource;

    public CarRepairInfoRepositoryImpl(DataSource integrationDataDataSource) {
        this.integrationDataDataSource = integrationDataDataSource;
    }

    @Override
    public List<Map<String, Object>> getAllCarRepairs(String currentDate) {
        return getData(currentDate, SQL1);
    }

    public List<Map<String, Object>> getAllCarWheelThicknesses(String currentDate) {
        return getData(currentDate, SQL2);
    }

    private List<Map<String, Object>> getData(String currentDate, String sql2) {
        try (Connection connection = integrationDataDataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql2)) {
                preparedStatement.setString(1, currentDate);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    return JdbcUtils.parseResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
