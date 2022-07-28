package com.uraltrans.logisticparamservice.service.schedule;

import com.uraltrans.logisticparamservice.service.postgres.abstr.ClientOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleClientOrderService {
    private final ClientOrderService clientOrderService;

    @Scheduled(cron = "0 0 4 * * *")
    public void loadClientOrders(){
        clientOrderService.saveAllClientOrders();
    }
}
