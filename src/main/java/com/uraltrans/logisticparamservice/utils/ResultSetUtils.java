package com.uraltrans.logisticparamservice.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultSetUtils {
    public static List<Map<String, Object>> getAllData(ResultSet rs) throws SQLException {
        List<Map<String, Object>> data = new ArrayList<>();
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
