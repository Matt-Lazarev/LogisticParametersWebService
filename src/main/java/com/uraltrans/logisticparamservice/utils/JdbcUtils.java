package com.uraltrans.logisticparamservice.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JdbcUtils {
    public static List<Map<String, Object>> getAllData(Connection connection, String SQL) throws SQLException {
        List<Map<String, Object>> data = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery(SQL)) {
                return getDataFromResultSet(rs, data);
            }
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

