package com.uraltrans.logisticparamservice.repository.itr;

import com.uraltrans.logisticparamservice.utils.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
public class RawSecondEmptyFlightRepositoryImpl implements RawSecondEmptyFlightRepository {

    private static final String SQL = "EXEC Dashboard.Flight_ ?, ?";

    @Resource(name = "itrDataSource")
    private final DataSource itrDataSource;

    public RawSecondEmptyFlightRepositoryImpl(DataSource itrDataSource) {
        this.itrDataSource = itrDataSource;
    }

    @Override
    public List<Map<String, Object>> getRawSecondEmptyFlightData(String from, String to) {
        return JdbcUtils.getAllDataWithParams(itrDataSource, SQL, from, to);
    }
}
