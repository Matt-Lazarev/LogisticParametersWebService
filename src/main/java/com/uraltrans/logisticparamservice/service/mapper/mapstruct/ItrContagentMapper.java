package com.uraltrans.logisticparamservice.service.mapper.mapstruct;

import com.uraltrans.logisticparamservice.entity.itr.projection.ItrContagentProjection;
import com.uraltrans.logisticparamservice.entity.postgres.Contragent;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItrContagentMapper {
    Contragent toContragent(ItrContagentProjection itrContagentProjection);

    default List<Contragent> toContragentList(List<ItrContagentProjection> itrFlights){
        return itrFlights.stream().map(this::toContragent).toList();
    }
}
