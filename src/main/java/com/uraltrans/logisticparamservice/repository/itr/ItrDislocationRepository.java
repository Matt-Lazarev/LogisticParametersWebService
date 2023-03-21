package com.uraltrans.logisticparamservice.repository.itr;

import com.uraltrans.logisticparamservice.entity.itr.ItrDislocation;
import com.uraltrans.logisticparamservice.entity.itr.projection.ItrDislocationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItrDislocationRepository extends JpaRepository<ItrDislocation, Integer> {

    @Query(
        value = """
            SELECT
            disl.CarNumber as CarNumber,
            disl.CarModel_CarVolume as Volume,
            disl.Dislocation_CodeStation as DislocationStation,
            disl.Dislocation_CodeFromStation as DepartureStation,
            disl.Dislocation_SendingStationName as DepartureStationName,
            disl.Dislocation_SourceRoadName as DepartureRoadName,
            disl.Dislocation_CodeDestStation as DestinationStation,
            disl.Dislocation_DestStationName as DestinationStationName,
            disl.Dislocation_DestRailWay as DestinationRoadName,
            disl.Dislocation_DateOFSending as DepartureDate,
            disl.Пр02 as P02,
            disl.Пр06 as P06,
            disl.Пр09 as P09,
            disl.Пр12 as P12,
            disl.Пр20 as P20,
            disl.CarStatus_State as StatusWagon,
            disl.ROsOwners_CarTypeName as WagonType,
            disl.Dislocation_IsFullText as FlightType,
            disl.Dislocation_CodeCargo as CargoId,
            disl.CarPassport_DaysBeforeDatePlanRepair as DaysBeforeDatePlanRepair,
            disl.Dislocation_DistanceFromCurrentStation as DistanceFromCurrentStation,
            disl.CarRun_RestRun as RestRun,
            disl.Dislocation_IdleOnTheCurrentStation as IdleDislocationStation
            FROM [DashBoard].Disl disl
            WHERE disl.ROsOwners_ManagerName = N'УРАЛЬСКАЯ ТРАНСПОРТНАЯ КОМПАНИЯ' AND
            disl.ROsOwners_CarTypeName = N'КР'
        """, nativeQuery = true)
    List<ItrDislocationProjection> getAllItrDislocations();
}
