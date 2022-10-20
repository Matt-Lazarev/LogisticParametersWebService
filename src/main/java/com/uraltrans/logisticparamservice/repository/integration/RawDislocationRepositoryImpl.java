package com.uraltrans.logisticparamservice.repository.integration;

import com.uraltrans.logisticparamservice.utils.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;

@Repository
public class RawDislocationRepositoryImpl implements RawDislocationRepository{
    private static final String SQL =
            "SELECT if47._Fld48 as CarNumber, if47._Fld2714 as Volume, " +
            "if47._Fld2845 as DislocationStationCode, " +
            "if47._Fld2846 as SourceStationCode, " +
            "if47._FLD2847 as DestinationStationCode, " +
            "if47._Fld2780 as SendDate, " +
            "if47._Fld177 as Feature2, " +
            "if47._Fld2709 as Feature6, " +
            "if47._Fld2844 as Feature9, " +
            "if47._Fld2712 as Feature12, " +
           // "if47._Fld2712 as Feature20, " + //FIXME
            "if47._Fld2784 as ClientNextTask, " +
            "if47._Fld234 as CarState, " +
            "if47._Fld235 as FleetState, " +
            "if47._Fld2848 as BeginOrderDate, " +
            "if47._Fld2849 as EndOrderDate, " +
            "if47._Fld51 as Loaded, " +
            "if47._Fld172 as WagonType, " +
            "if47._Fld2850 as Cargo, " +
            "if47._Fld2852 as CargoCode, " +
            "if47._Fld173 as Owner, " +
            "if47._Fld2777 as Operation, " +
            "if47._Fld52 as Model, " +
            "if47._Fld2783 as NextStation, " +
            "if47._Fld49 as DaysBeforeDatePlanRepair, " +
            "if47._Fld2719 as DistanceFromCurrentStation, " +
            "if47._Fld322 as RestRun " +
            "FROM _InfoRg47 if47 " +
            "WHERE if47._Fld171 = ? and " +
            "if47._Fld176 = N'УРАЛЬСКАЯ ТРАНСПОРТНАЯ КОМПАНИЯ' and " +
            "if47._Fld172 = N'КР'";
//            "if47._Fld51 = N'ГРУЖ'";

    @Resource(name = "integrationDataDataSource")
    private final DataSource integrationDataDataSource;

    public RawDislocationRepositoryImpl(DataSource integrationDataDataSource) {
        this.integrationDataDataSource = integrationDataDataSource;
    }

    @Override
    public List<Map<String, Object>> getAllDislocations(String dislocationDate) {
        try (Connection connection = integrationDataDataSource.getConnection()) {
            try(PreparedStatement preparedStatement = connection.prepareStatement(SQL)){
                preparedStatement.setString(1, dislocationDate);
                try(ResultSet rs = preparedStatement.executeQuery()){
                    return JdbcUtils.parseResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
