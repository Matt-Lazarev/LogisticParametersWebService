package com.uraltrans.logisticparamservice.repository.utcsrs;

import com.uraltrans.logisticparamservice.utils.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
public class RegisterSecondEmptyFlightRepositoryImpl implements RegisterSecondEmptyFlightRepository {
    private static final String SQL =
            "SELECT r45_1._Code as SourceStation, r45_2._Code as DestStation " +
            "FROM _InfoRg6810 inf " +
            "LEFT OUTER JOIN _Reference45 r45_1 on inf._Fld6811RRef = r45_1._IDRRef " +
            "LEFT OUTER JOIN _Reference45 r45_2 on inf._Fld6812RRef = r45_2._IDRRef " +
            "WHERE r45_1._Code = ? AND r45_2._Code = ?";

    @Resource(name = "utcsrsDataSource")
    private final DataSource utcsrsDataSource;

    public RegisterSecondEmptyFlightRepositoryImpl(DataSource utcsrsDataSource) {
        this.utcsrsDataSource = utcsrsDataSource;
    }


    @Override
    public boolean containsFlightsByCodes(String sourceStationCode, String destStationCode) {
        List<Map<String, Object>> data = JdbcUtils.getAllDataWithParams(utcsrsDataSource, SQL, sourceStationCode, destStationCode);
        return data.size() > 0;
    }
}
