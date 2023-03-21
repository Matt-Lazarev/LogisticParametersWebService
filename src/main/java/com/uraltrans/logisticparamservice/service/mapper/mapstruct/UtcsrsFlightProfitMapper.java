package com.uraltrans.logisticparamservice.service.mapper.mapstruct;

import com.uraltrans.logisticparamservice.entity.postgres.FlightProfit;
import com.uraltrans.logisticparamservice.entity.utcsrs.projection.UtcstsFlightProfitProjection;
import com.uraltrans.logisticparamservice.utils.MappingUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UtcsrsFlightProfitMapper {

    FlightProfit toFlightProfit(UtcstsFlightProfitProjection utcstsFlightProfit);
    List<FlightProfit> toFlightProfitList(List<UtcstsFlightProfitProjection> utcstsFlightProfits);

    @AfterMapping
    default void flightProfitPostConstruct(@MappingTarget FlightProfit flightProfit){
        flightProfit.setSendDate(MappingUtils.fix1cDate(flightProfit.getSendDate()));
        flightProfit.setFlightAmount(1);
    }
}
