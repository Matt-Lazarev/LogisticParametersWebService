package com.uraltrans.logisticparamservice.repository.integration;

import com.uraltrans.logisticparamservice.entity.integration.IntegrationDislocation;
import com.uraltrans.logisticparamservice.entity.integration.projection.IntegrationDislocationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IntegrationDislocationRepository extends JpaRepository<IntegrationDislocation, Integer> {
    @Query(
        value = """
            SELECT
            if47._Fld48 as CarNumber,
            if47._Fld2714 as Volume,
            if47._Fld2845 as DislocationStationCode,
            if47._Fld2846 as SourceStationCode,
            if47._Fld54 as SourceStation,
            if47._Fld232 as SourceStationRoad,
            if47._FLD2847 as DestinationStationCode,
            if47._FLD56 as DestinationStation,
            if47._FLD57 as DestinationStationRoad,
            if47._Fld2780 as SendDate,
            if47._Fld177 as Feature2,
            if47._Fld2709 as Feature6,
            if47._Fld2844 as Feature9,
            if47._Fld2712 as Feature12,
            if47._Fld2858 as Feature20,
            if47._Fld2784 as ClientNextTask,
            if47._Fld2859 as ManagerNextTask,
            if47._Fld2860 as OperationDateTime,
            if47._Fld234 as CarState,
            if47._Fld235 as FleetState,
            if47._Fld2848 as CurrentOrderBegin,
            if47._Fld2849 as CurrentOrderEnd,
            if47._Fld51 as Loaded,
            if47._Fld172 as WagonType,
            if47._Fld2850 as Cargo,
            if47._Fld2852 as CargoCode,
            if47._Fld173 as Owner,
            if47._Fld2777 as Operation,
            if47._Fld52 as Model,
            if47._Fld2783 as NextStation,
            if47._Fld49 as DaysBeforeDatePlanRepair,
            if47._Fld2719 as DistanceFromCurrentStation,
            if47._Fld322 as RestRun,
            if47._Fld2720 as IdleDislocationStation
            FROM _InfoRg47 if47
            WHERE if47._Fld171 = :dislocationDate and
            if47._Fld176 = N'УРАЛЬСКАЯ ТРАНСПОРТНАЯ КОМПАНИЯ' and
            if47._Fld172 = N'КР'
        """, nativeQuery = true)
    List<IntegrationDislocationProjection> getAllIntegrationDislocations(String dislocationDate);
}
