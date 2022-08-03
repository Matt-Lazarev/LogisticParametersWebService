package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.ClientOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleClientOrderService {
    private final ClientOrderService clientOrderService;

    public void loadClientOrders(){
        clientOrderService.saveAllClientOrders();
    }
}
