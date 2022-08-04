package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.ClientOrder;
import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.repository.itr.RawClientOrderRepository;
import com.uraltrans.logisticparamservice.repository.postgres.ClientOrderRepository;
import com.uraltrans.logisticparamservice.service.mapper.ClientOrderMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ClientOrderService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientOrderServiceImpl implements ClientOrderService {
    private final RawClientOrderRepository rawClientOrderRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final ClientOrderMapper clientOrderMapper;
    private final LoadParameterService loadParameterService;

    @Override
    public List<ClientOrder> getAllClientOrders() {
        return clientOrderRepository.findAll();
    }

    @Override
    public void saveAllClientOrders() {
        prepareNextSave();
        LoadParameters params = loadParameterService.getLoadParameters();
        List<Map<String, Object>> clientOrders =
                rawClientOrderRepository.getAllOrders(params.getStatus(), params.getCarType(), getFirstDayOfMonthDate());
        List<ClientOrder> orders = clientOrderMapper.mapRawDataToClientOrderList(clientOrders);
        orders = filterEmployeesAndCarsAmount(orders);
        clientOrderRepository.saveAll(orders);
    }

    @Override
    public ClientOrder findByStationCodesAndVolume(String sourceStation, String destStation, BigDecimal volumeFrom, BigDecimal volumeTo) {
        return clientOrderRepository.findByStationCodesAndVolume(sourceStation, destStation, volumeFrom, volumeTo);
    }

    private void prepareNextSave(){
        clientOrderRepository.truncate();
    }

    private String getFirstDayOfMonthDate(){
        LocalDate now = LocalDate.now();
        return LocalDate.of(now.getYear(), now.getMonth(), 1).toString();
    }

    private List<ClientOrder> filterEmployeesAndCarsAmount(List<ClientOrder> clientOrders){
        List<String> managers = loadParameterService.getManagers();
        return clientOrders
                .stream()
                .filter(clientOrder -> managers.contains(clientOrder.getManager()))
                .filter(clientOrder -> clientOrder.getCarsAmount() != null && clientOrder.getCarsAmount() > 0)
                .collect(Collectors.toList());
    }
}
