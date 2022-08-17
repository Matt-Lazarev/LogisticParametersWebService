package com.uraltrans.logisticparamservice.service.mapper;

import com.uraltrans.logisticparamservice.entity.postgres.ClientOrder;
import com.uraltrans.logisticparamservice.utils.Mapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClientOrderMapper {
    public List<ClientOrder> mapRawDataToClientOrderList(List<Map<String, Object>> rawClientOrdersData){
        return rawClientOrdersData
                .stream()
                .map(this::mapToOrderClient)
                .collect(Collectors.toList());
    }

    private ClientOrder mapToOrderClient(Map<String, Object> data) {
        return ClientOrder.builder()
                .carNumber((String) data.get("CarNumber"))
                .status((String) data.get("DocumentStateName"))
                .carType((String) data.get("CarTypeName"))
                .volumeFrom(Mapper.toBigDecimal((Double) data.get("VolumeFrom")))
                .volumeTo(Mapper.toBigDecimal((Double) data.get("VolumeTo")))
                .client((String) data.get("Клиент"))
                .sourceStationCode6((String) data.get("StationCode6From"))
                .destinationStationCode6((String) data.get("StationCode6To"))
                .cargo((String) data.get("Груз"))
                .cargoCode((String) data.get("Код груза"))
                .carsAmount(((BigDecimal) data.get("Количество вагонов")).intValue())
                .manager((String) data.get("Менеджер"))
                .fromDate(((Timestamp) data.get("Date_B")).toLocalDateTime())
                .toDate(((Timestamp) data.get("Date_e")).toLocalDateTime())
                .utRate((BigDecimal) data.get("UT_Rate"))
                .build();
    }
}
