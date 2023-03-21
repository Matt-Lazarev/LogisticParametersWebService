package com.uraltrans.logisticparamservice.service.mapper.mapstruct;

import com.uraltrans.logisticparamservice.entity.itr.projection.ItrClientOrderProjection;
import com.uraltrans.logisticparamservice.entity.postgres.ClientOrder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItrClientOrderMapper {
    ClientOrder toClientOrder(ItrClientOrderProjection itrClientOrder);

    default List<ClientOrder> toClientOrderList(List<ItrClientOrderProjection> itrClientOrders){
        return itrClientOrders.stream().map(this::toClientOrder).toList();
    }
}
