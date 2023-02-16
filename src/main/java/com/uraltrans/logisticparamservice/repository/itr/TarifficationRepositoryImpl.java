package com.uraltrans.logisticparamservice.repository.itr;

import com.uraltrans.logisticparamservice.utils.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
public class TarifficationRepositoryImpl implements TarifficationRepository {
    // Условия тарификации (УТ)   2023-02-01
    private static final String TARIFFICATION_SQL = "exec dbo.ReportKlientStavka @Param=1, @DB = ?, @DE = ?";

    // Таблица расстояний (ТР)
    private static final String DISTANCES_SQL =
            "select " +
                    "AGR_DISTANCE.AID as [AGR_DISTANCE_AID], " +
                    "AGR_DISTANCE.D1 as [AGR_DISTANCE_D1], " +
                    "AGR_DISTANCE.D2 as [AGR_DISTANCE_D2], " +
                    "AGR_DISTANCE.SUMMA as [AGR_DISTANCE_SUMMA], " +
                    "AGR_DISTANCE.ID_KL_PREDMET as [AGR_DISTANCE_ID_KL_PREDMET], " +
                    "AGR_DISTANCE.KL1 as [AGR_DISTANCE_KL1], " +
                    "AGR_DISTANCE.KL2 as [AGR_DISTANCE_KL2], " +
                    "AGR_DISTANCE.KL3 as [AGR_DISTANCE_KL3], " +
                    "AGR_DISTANCE.KLASS as [AGR_DISTANCE_KLASS] " +
                    "from AGR_DISTANCE " +
                    "where ID_KL_PREDMET in (%s)";


    // Станции отправления
    private static final String SOURCE_STATIONS_SQL =
            "select " +
                    "KL_PRED_STATIONS_FROM.ID as [KL_PRED_STATIONS_FROM_ID], " +
                    "KL_PRED_STATIONS_FROM.ID_KL_PREDMET as [KL_PRED_STATIONS_FROM_ID_KL_PREDMET], " +
                    "tt2.NAME as [tt2_NAME], tt2.RAILWAY, " +
                    "KL_PRED_STATIONS_FROM.ID_STATION  AS  KL_PRED_STATIONS_FROM_ID_STATION, " +
                    "KL_PRED_STATIONS_FROM.NOT_IN as [KL_PRED_STATIONS_FROM_NOT_IN] " +
                    "from KL_PRED_STATIONS_FROM " +
                    "LEFT JOIN STATION tt2 ON KL_PRED_STATIONS_FROM.ID_STATION = tt2.AID " +
                    "where ID_KL_PREDMET in (%s)";

    // Станции назначения
    private static final String DEST_STATIONS_SQL =
            "select " +
                    "tt0.Name as [tt0_Name], " +
                    "KL_PRED_STATIONS_TO.ID as [KL_PRED_STATIONS_TO_ID], " +
                    "KL_PRED_STATIONS_TO.ID_KL_PREDMET as [KL_PRED_STATIONS_TO_ID_KL_PREDMET], " +
                    "tt3.NAME as [tt3_NAME]," +
                    "tt3.RailWay, " +
                    "KL_PRED_STATIONS_TO.NOT_IN as [KL_PRED_STATIONS_TO_NOT_IN], " +
                    "KL_PRED_STATIONS_TO.CargoID as [KL_PRED_STATIONS_TO_CargoID], " +
                    "KL_PRED_STATIONS_TO.X as [KL_PRED_STATIONS_TO_X], " +
                    "KL_PRED_STATIONS_TO.Y as [KL_PRED_STATIONS_TO_Y], " +
                    "KL_PRED_STATIONS_TO.ID_STATION as [KL_PRED_STATIONS_TO_ID_STATION], " +
                    "KL_PRED_STATIONS_TO.ISTEMP as [KL_PRED_STATIONS_TO_ISTEMP], " +
                    "KL_PRED_STATIONS_TO.Z as [KL_PRED_STATIONS_TO_Z], " +
                    "KL_PRED_STATIONS_TO.ID_STATION_FROM as [KL_PRED_STATIONS_TO_ID_STATION_FROM], " +
                    "tt4.NAME as [tt4_NAME], " +
                    "tt4.RailWay as [tt4_RailWay], " +
                    "KL_PRED_STATIONS_TO.idsCarType, " +

                    "dbo.GetCarTypeNames(KL_PRED_STATIONS_TO.idsCarType) as CartType_Names, " +
                    "dbo.GET_KL_PRED_STATIONS_TO_STATIONS_EXCLUDE(KL_PRED_STATIONS_TO.ID,1,1) as StationsExcludeFromIDs, " +
                    "dbo.GET_KL_PRED_STATIONS_TO_STATIONS_EXCLUDE(KL_PRED_STATIONS_TO.ID,1,2) as StationsExcludeFromNames, " +
                    "dbo.GET_KL_PRED_STATIONS_TO_STATIONS_EXCLUDE(KL_PRED_STATIONS_TO.ID,2,1) as StationsExcludeToIDs, " +
                    "dbo.GET_KL_PRED_STATIONS_TO_STATIONS_EXCLUDE(KL_PRED_STATIONS_TO.ID,2,2) as StationsExcludeToNames, " +
                    "dbo.GET_KL_PRED_STATIONS_TO_CARGOS_EXCLUDE(KL_PRED_STATIONS_TO.ID,1) as CargosExcludeIDs, " +
                    "dbo.GET_KL_PRED_STATIONS_TO_CARGOS_EXCLUDE(KL_PRED_STATIONS_TO.ID,2) as CargosExcludeNames " +
                    "from KL_PRED_STATIONS_TO " +
                    "LEFT JOIN STATION tt4 " +
                    "ON KL_PRED_STATIONS_TO.ID_STATION_FROM =tt4.AID " +
                    "LEFT JOIN qCargo tt0 " +
                    "ON KL_PRED_STATIONS_TO.CargoID =tt0.AID " +
                    "LEFT JOIN STATION tt3 " +
                    "ON KL_PRED_STATIONS_TO.ID_STATION =tt3.AID " +
                    "where ID_KL_PREDMET in (%s)";

    // Заграничные станции
    private static final String FOREIGN_STATIONS_SQL = "exec Calc.KlientPredSelectRoute ?, 1";


    // ИсключаяВагоны:
    private static final String EXCLUDED_WAGONS_SQL =
            "select k.* " +
                    "from KL_PRED_NCN k " +
                    "where k.ID_KL_PRED in (%s)";

    // ВключаяВагоны:
    private static final String INCLUDED_WAGONS_SQL =
            "select k.* " +
                    "from KL_PRED_CN k " +
                    "where k.ID_KL_PRED in (%s)";

    //Дополнительные поля
    private static final String ADDITIONAL_COLUMNS1_SQL =
            "select " +
                    "ID_KL_PRED, " +
                    "KL_PRED_TRANSKIND.ID_TRANSKIND as KL_PRED_TRANSKIND_ID_TRANSKIND " +
                    "from " +
                    "KL_PRED_TRANSKIND " +
                    "where ID_KL_PRED = %d";

    private static final String ADDITIONAL_COLUMNS2_SQL =
            "select Aid, Id_Doc " +
                    "from DO_Document " +
                    "where Aid = %d";

    private static final String ADDITIONAL_COLUMNS3_SQL =
            "exec Deal.Agreement_AnnexList @filterByAgrID=%d, @docTypeID=%d";

    @Resource(name = "itrDataSource")
    private final DataSource itrDataSource;

    public TarifficationRepositoryImpl(DataSource itrDataSource) {
        this.itrDataSource = itrDataSource;
    }

    @Override
    public List<Map<String, Object>> getAllTariffications(String dateFrom, String dateTo) {
        return JdbcUtils.getAllDataWithParams(itrDataSource, TARIFFICATION_SQL, dateFrom, dateTo);
    }

    @Override
    public List<Map<String, Object>> getAllDistances(String tarifficationIds) {
        return JdbcUtils.getAllData(itrDataSource, String.format(DISTANCES_SQL, tarifficationIds));
    }

    @Override
    public List<Map<String, Object>> getAllSourceStations(String tarifficationIds) {
        return JdbcUtils.getAllData(itrDataSource, String.format(SOURCE_STATIONS_SQL, tarifficationIds));
    }

    @Override
    public List<Map<String, Object>> getAllDestStations(String tarifficationIds) {
        return JdbcUtils.getAllData(itrDataSource, String.format(DEST_STATIONS_SQL, tarifficationIds));
    }

    @Override
    public List<Map<String, Object>> getAllForeignStations(Integer tarifficationId) {
        return JdbcUtils.getAllDataWithIntParam(itrDataSource, FOREIGN_STATIONS_SQL, tarifficationId);
    }

    @Override
    public List<Map<String, Object>> getAllExcludedStations(String tarifficationIds) {
        return JdbcUtils.getAllData(itrDataSource, String.format(EXCLUDED_WAGONS_SQL, tarifficationIds));
    }

    @Override
    public List<Map<String, Object>> getAllIncludedStations(String tarifficationIds) {
        return JdbcUtils.getAllData(itrDataSource, String.format(INCLUDED_WAGONS_SQL, tarifficationIds));
    }

    @Override
    public List<Map<String, Object>> getAllAdditionalColumns1(Integer tarifficationId){
        return JdbcUtils.getAllData(itrDataSource, String.format(ADDITIONAL_COLUMNS1_SQL, tarifficationId));
    }

    @Override
    public List<Map<String, Object>> getAllAdditionalColumns2(Integer parentDocumentId){
        return JdbcUtils.getAllData(itrDataSource, String.format(ADDITIONAL_COLUMNS2_SQL, parentDocumentId));
    }

    @Override
    public List<Map<String, Object>> getAllAdditionalColumns3(Integer argId, Integer docId) {
        return JdbcUtils.getAllData(itrDataSource, String.format(ADDITIONAL_COLUMNS3_SQL, argId, docId));
    }
}
