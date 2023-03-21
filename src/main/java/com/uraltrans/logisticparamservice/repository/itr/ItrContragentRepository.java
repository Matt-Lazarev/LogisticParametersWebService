package com.uraltrans.logisticparamservice.repository.itr;

import com.uraltrans.logisticparamservice.entity.itr.projection.ItrContagentProjection;
import com.uraltrans.logisticparamservice.entity.itr.ItrContragent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItrContragentRepository extends JpaRepository<ItrContragent, Integer> {
    @Query(
        value = """
            select k.NAME as company, kc.EMAIL as email, kc.NAME as name, kc.AgentINN as inn, t.name as type
            from  kontragent_contakt kc
            inner join CRM.TypeOfContact t on kc.typ=t.AID
            inner join KONTRAGENT k on k.AID = kc.ID_KONTRAGENT
            where t.name = :type
        """, nativeQuery = true)
    List<ItrContagentProjection> getAllItrContragents(String type);
}
