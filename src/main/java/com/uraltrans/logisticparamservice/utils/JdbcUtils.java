package com.uraltrans.logisticparamservice.utils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JdbcUtils {
    public static List<Map<String, Object>> getAllData(DataSource dataSource, String SQL) {
        List<Map<String, Object>> data = new ArrayList<>();
        try(Connection connection = dataSource.getConnection()){
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rs = statement.executeQuery(SQL)) {
                    return getDataFromResultSet(rs, data);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Map<String, Object>> getAllDataWithParams(DataSource dataSource, String SQL, String... params){
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                for(int i=1; i<=params.length; i++){
                    preparedStatement.setString(i, params[i-1]);
                }
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    return JdbcUtils.parseResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Map<String, Object>> getAllDataWithParams(DataSource dataSource, String SQL, String date, Integer carNumber){
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setString(1, date);
                preparedStatement.setInt(2, carNumber);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    return JdbcUtils.parseResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Map<String, Object>> parseResultSet(ResultSet rs) throws SQLException {
        List<Map<String, Object>> data = new ArrayList<>();
        return getDataFromResultSet(rs, data);
    }

    private static List<Map<String, Object>> getDataFromResultSet(ResultSet rs, List<Map<String, Object>> data) throws SQLException {
        while (rs.next()) {
            Map<String, Object> resMap = new LinkedHashMap<>();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                resMap.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
            }
            data.add(resMap);
        }
        return data;
    }
}

