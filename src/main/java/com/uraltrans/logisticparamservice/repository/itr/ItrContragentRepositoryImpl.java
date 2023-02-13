package com.uraltrans.logisticparamservice.repository.itr;

import com.uraltrans.logisticparamservice.utils.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
public class ItrContragentRepositoryImpl implements ItrContragentRepository {
    private static final String SQL =
            "select " +
            "k.NAME as Company, " +
            "kc.EMAIL as Email, " +
            "kc.NAME as Name, " +
            "kc.AgentINN as INN, " +
            "t.name as Type " +
            "from  kontragent_contakt kc " +
            "inner join CRM.TypeOfContact t on kc.typ=t.AID " +
            "inner join KONTRAGENT k on k.AID = kc.ID_KONTRAGENT " +
            "where t.name = ?";

    @Resource(name = "itrDataSource")
    private final DataSource itrDataSource;

    public ItrContragentRepositoryImpl(DataSource itrDataSource) {
        this.itrDataSource = itrDataSource;
    }

    @Override
    public List<Map<String, Object>> getAllContragentsByType(String type) {
        return JdbcUtils.getAllDataWithParams(itrDataSource, SQL, type);
    }
}
