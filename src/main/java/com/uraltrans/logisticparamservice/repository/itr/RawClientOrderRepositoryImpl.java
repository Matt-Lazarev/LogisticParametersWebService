package com.uraltrans.logisticparamservice.repository.itr;

import com.uraltrans.logisticparamservice.utils.FileUtils;
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
public class RawClientOrderRepositoryImpl implements RawClientOrderRepository{
    private static final String SQL;

    static {
        SQL = FileUtils.getClientOrdersSqlScript();
    }

    @Resource(name = "itrDataSource")
    private final DataSource itrDataSource;

    public RawClientOrderRepositoryImpl(DataSource itrDataSource) {
        this.itrDataSource = itrDataSource;
    }

    @Override
    public List<Map<String, Object>> getAllOrders(String status, String carType, String dateFrom) {
        try {
            Connection connection = itrDataSource.getConnection();
            try(PreparedStatement preparedStatement = connection.prepareStatement(SQL)){
                preparedStatement.setString(1, status);
                preparedStatement.setString(2, carType);
                preparedStatement.setString(3, dateFrom);
                ResultSet rs = preparedStatement.executeQuery();
                return JdbcUtils.parseResultSet(rs);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
