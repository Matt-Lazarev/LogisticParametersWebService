package com.uraltrans.logisticparamservice.repository.itr;

import com.uraltrans.logisticparamservice.utils.ResultSetUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
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

    @Resource(name = "itrDataSource")
    private final DataSource itrDataSource;

    public RawFlightRepositoryImpl(DataSource itrDataSource) {
        this.itrDataSource = itrDataSource;
    }

    @Override
    public List<Map<String, Object>> getAllFlightsBetween(String from, String to) {
        try {
            Connection connection = itrDataSource.getConnection();
            try(PreparedStatement preparedStatement = connection.prepareStatement("EXEC Dashboard.Flight_ ?, ?")){
                preparedStatement.setString(1, from);
                preparedStatement.setString(2, to);
                try(ResultSet rs = preparedStatement.executeQuery()){
                    return ResultSetUtils.getAllData(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
