package com.uraltrans.logisticparamservice.repository.utcsrs;

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
public class RawFlightProfitRepositoryImpl implements RawFlightProfitRepository {
    private static final String SQL =
            """
            SELECT r45_1._Code as _SourceStationCode, r45_2._Code as _DestStationCode,
                   r33._Code as _CargoCode, r33._Fld5637 as _Cargo,
                   inf._Fld6441 as _TotalProfit, r22._Code as _Currency,
                   inf._Fld6629 as _Volume, inf._Fld6626 as _SendDate
            FROM _InfoRg5865 inf
            LEFT OUTER JOIN _Reference45 r45_1 on inf._Fld6583RRef = r45_1._IDRRef
            LEFT OUTER JOIN _Reference45 r45_2 on inf._Fld6584RRef = r45_2._IDRRef
            LEFT OUTER JOIN _Reference33 r33 on inf._Fld6585RRef = r33._IDRRef
            LEFT OUTER JOIN _Reference22 r22 on inf._Fld6578RRef = r22._IDRRef
            WHERE inf._Fld6626 >= ?
            """;

    @Resource(name = "utcsrsDataSource")
    private final DataSource utcsrsDataSource;

    public RawFlightProfitRepositoryImpl(DataSource utcsrsDataSource) {
        this.utcsrsDataSource = utcsrsDataSource;
    }

    @Override
    public List<Map<String, Object>> getAllFlightProfits(String fromDate) {
        try (Connection connection = utcsrsDataSource.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, fromDate);
            try(ResultSet rs = preparedStatement.executeQuery()){
                return JdbcUtils.parseResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
