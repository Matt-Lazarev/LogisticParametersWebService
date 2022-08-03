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
    public List<Map<String, Object>> getAllFlightsBetween(String[][] fromToDatePairs) {
        try (Connection connection = itrDataSource.getConnection()) {
            List<Map<String, Object>> result = new ArrayList<>();
            for(String[] fromTo : fromToDatePairs){
                try(PreparedStatement preparedStatement = connection.prepareStatement(SQL)){
                    preparedStatement.setString(1, fromTo[0]);
                    preparedStatement.setString(2, fromTo[1]);
                    try(ResultSet rs = preparedStatement.executeQuery()){
                        result.addAll(JdbcUtils.parseResultSet(rs));
                    }
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
