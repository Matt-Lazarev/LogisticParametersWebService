package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.cargo.CargoDto;
import com.uraltrans.logisticparamservice.entity.itr.projection.ItrClientOrderProjection;
import com.uraltrans.logisticparamservice.entity.postgres.ClientOrder;
import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.repository.itr.ItrClientOrderRepository;
import com.uraltrans.logisticparamservice.repository.postgres.ClientOrderRepository;
import com.uraltrans.logisticparamservice.service.mapper.mapstruct.ItrClientOrderMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ClientOrderService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientOrderServiceImpl implements ClientOrderService {
    private final ItrClientOrderRepository itrClientOrderRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final ItrClientOrderMapper itrClientOrderMapper;
    private final LoadParameterService loadParameterService;

    @Override
    public List<ClientOrder> getAllClientOrders() {
        return clientOrderRepository.findAll();
    }

    @Override
    public void saveAllClientOrders() {
        prepareNextSave();
        LoadParameters params = loadParameterService.getLoadParameters();
        List<ItrClientOrderProjection> clientOrders =
                itrClientOrderRepository.getAllOrders(params.getStatus(), params.getCarType(), getFirstDayOfMonthDate());
        List<ClientOrder> orders = itrClientOrderMapper.toClientOrderList(clientOrders);
        orders = filterEmployeesAndCarsAmount(orders);
        clientOrderRepository.saveAll(orders);
    }

    @Override
    public List<CargoDto> findBySourceStationCodeAndVolume(String sourceStation, BigDecimal volume) {
        return clientOrderRepository.findBySourceStationCodeAndVolume(sourceStation, volume);
    }

    @Override
    public BigDecimal findUtRateByStationCodesAndVolume(String sourceStation, String destStation, BigDecimal volume) {
        return clientOrderRepository.findUtRateByStationCodesAndVolume(sourceStation, destStation, volume);
    }

    @Override
    public BigDecimal findUtRateBySourceStationCodeAndVolume(String sourceStation, BigDecimal volume) {
        return clientOrderRepository.findUtRateBySourceStationCodeAndVolume(sourceStation, volume);
    }

    private void prepareNextSave(){
        clientOrderRepository.truncate();
    }

    private String getFirstDayOfMonthDate(){
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).toString();
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
