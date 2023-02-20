package com.uraltrans.logisticparamservice.repository.itr;

import com.uraltrans.logisticparamservice.utils.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
public class RawItrDislocationRepositoryImpl implements RawItrDislocationRepository {

    private static final String SQL =
            """
            SELECT
            disl.CarNumber as CarNumber,
            disl.CarModel_CarVolume as Volume,
            disl.Dislocation_CodeStation as DislocationStationCode,
            disl.Dislocation_CodeFromStation as SourceStationCode,
            disl.Dislocation_SendingStationName as SourceStation,
            disl.Dislocation_SourceRoadName as SourceStationRoad,
            disl.Dislocation_CodeDestStation as DestinationStationCode,
            disl.Dislocation_DestStationName as DestinationStation,
            disl.Dislocation_DestRailWay as DestinationStationRoad,
            disl.Dislocation_DateOFSending as SendDate,
            disl.Пр02 as Feature2,
            disl.Пр06 as Feature6,
            disl.Пр09 as Feature9,
            disl.Пр12 as Feature12,
            disl.Пр20 as Feature20,
            disl.CarStatus_State as CarState,
            disl.ROsOwners_CarTypeName as WagonType,
            disl.Dislocation_IsFullText as FlightType,
            disl.Dislocation_CodeCargo as CargoCode,
            disl.CarPassport_DaysBeforeDatePlanRepair as DaysBeforeDatePlanRepair,
            disl.Dislocation_DistanceFromCurrentStation as DistanceFromCurrentStation,
            disl.CarRun_RestRun as RestRun,
            disl.Dislocation_IdleOnTheCurrentStation as IdleDislocationStation
            FROM [DashBoard].Disl disl
            WHERE disl.ROsOwners_ManagerName = N'УРАЛЬСКАЯ ТРАНСПОРТНАЯ КОМПАНИЯ' AND
            disl.ROsOwners_CarTypeName = N'КР'
            """;


    @Resource(name = "itrDataSource")
    private final DataSource itrDataSource;

    public RawItrDislocationRepositoryImpl(DataSource itrDataSource) {
        this.itrDataSource = itrDataSource;
    }

    @Override
    public List<Map<String, Object>> getAllDislocations() {
        return JdbcUtils.getAllData(itrDataSource, SQL);
    }
}
