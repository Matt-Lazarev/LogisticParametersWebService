package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.FlightProfit;

import java.util.List;

public interface FlightProfitService {
    void saveAll();
    List<FlightProfit> getAllFlightProfits();
}
