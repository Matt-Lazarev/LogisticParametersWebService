package com.uraltrans.logisticparamservice.repository.itr;

import com.uraltrans.logisticparamservice.utils.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class RawFlightRepositoryImpl implements RawFlightRepository {
    private static final String SQL = "EXEC Dashboard.Flight_ ?, ?";

    @Resource(name = "itrDataSource")
    private final DataSource itrDataSource;

    public RawFlightRepositoryImpl(DataSource itrDataSource) {
        this.itrDataSource = itrDataSource;
    }

    @Override
    public List<Map<String, Object>> getAllFlightsBetween(String[] fromToPair) {
        try (Connection connection = itrDataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setString(1, fromToPair[0]);
                preparedStatement.setString(2, fromToPair[1]);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    return JdbcUtils.parseResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
