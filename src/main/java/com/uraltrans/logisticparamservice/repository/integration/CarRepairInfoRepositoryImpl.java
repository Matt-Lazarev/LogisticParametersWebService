package com.uraltrans.logisticparamservice.repository.integration;

import com.uraltrans.logisticparamservice.utils.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class CarRepairInfoRepositoryImpl implements CarRepairInfoRepository {
    private static final String SQL_ALL_CAR_REPAIR_INFO =
                     """
                        SELECT _Code as CarNumber, _Fld2701 as NonworkingPark,
                        _Fld2702 as Refurbished, _Fld2703 as Rejected
                        FROM _InfoRg2696 i2696
                        INNER JOIN _Reference134 r134
                        ON r134._IDRRef =  i2696._Fld2697RRef
                        WHERE i2696._Fld2698 = ?
                    """;


    private static final String SQL_CURRENT_CAR_REPAIR_INFO =
                    """
                        SELECT _Fld2701 as NonworkingPark,
                        _Fld2702 as Refurbished,
                        _Fld2703 as Rejected,
                        _Fld2747 as RequiresRepair
                        FROM _InfoRg2696 i2696
                        INNER JOIN _Reference134 r134
                        ON r134._IDRRef =  i2696._Fld2697RRef
                        WHERE i2696._Fld2698 = ? and _Code = ?
                    """;


    private static final String SQL_THICKNESS_INFO =
                    """
                        SELECT _Code as CarNumber, _Fld284 as ThicknessWheel, _Fld285 as ThicknessComb
                        FROM _InfoRg281 i281
                        INNER JOIN _Reference134 r134
                        ON r134._IDRRef = i281._Fld282RRef
                        WHERE i281._Fld283 >= ?
                    """;


    @Resource(name = "integrationDataDataSource")
    private final DataSource integrationDataDataSource;

    public CarRepairInfoRepositoryImpl(DataSource integrationDataDataSource) {
        this.integrationDataDataSource = integrationDataDataSource;
    }

    @Override
    public List<Map<String, Object>> getAllCarRepairs(String currentDate) {
        return getData(currentDate, SQL_ALL_CAR_REPAIR_INFO);
    }

    @Override
    public Map<String, Object> getCarRepairByDate(String currentDate, Integer carNumber) {
        List<Map<String, Object>> result = getDataWithParams(currentDate, carNumber, SQL_CURRENT_CAR_REPAIR_INFO);
        return result.size() != 0 ? result.get(0) : Collections.emptyMap();
    }

    @Override
    public List<Map<String, Object>> getAllCarWheelThicknesses(String currentDate) {
        return getData(currentDate, SQL_THICKNESS_INFO);
    }

    private List<Map<String, Object>> getData(String currentDate, String SQL) {
        return JdbcUtils.getAllDataWithParams(integrationDataDataSource, SQL, currentDate);
    }

    private List<Map<String, Object>> getDataWithParams(String currentDate, Integer carNumber, String SQL) {
        return JdbcUtils.getAllDataWithParams(integrationDataDataSource, SQL, currentDate, carNumber);
    }
}
