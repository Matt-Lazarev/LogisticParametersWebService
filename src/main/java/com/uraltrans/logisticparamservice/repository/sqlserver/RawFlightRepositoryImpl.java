package com.uraltrans.logisticparamservice.repository.sqlserver;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RawFlightRepositoryImpl implements RawFlightRepository {

    @Qualifier("sqlServerDataSource")
    private final DataSource sqlServerDataSource;

    public RawFlightRepositoryImpl(DataSource sqlServerDataSource) {
        this.sqlServerDataSource = sqlServerDataSource;
    }

    @Override
    public List<Map<String, Object>> getAllFlightsBetween(String from, String to) {
        List<Map<String, Object>> flights = new ArrayList<>();
        try {
            Connection connection = sqlServerDataSource.getConnection();
            try(PreparedStatement preparedStatement = connection.prepareStatement("EXEC Dashboard.Flight_ ?, ?")){
              //  preparedStatement.setString(1, from);
              //  preparedStatement.setString(2, to);
                  preparedStatement.setString(1, "2022-05-01");
                  preparedStatement.setString(2, "2022-05-02");
                try(ResultSet rs = preparedStatement.executeQuery()){
                    while (rs.next()) {
                        Map<String, Object> resMap = new LinkedHashMap<>();
                        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                            resMap.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                        }
                        flights.add(resMap);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return flights;
    }
}
