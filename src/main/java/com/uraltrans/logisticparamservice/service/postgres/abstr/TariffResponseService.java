package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.dto.ratetariff.TariffResultResponse;
import com.uraltrans.logisticparamservice.entity.postgres.TariffResponse;

import java.util.List;

public interface TariffResponseService {
    void saveAllTariffResponses(TariffResultResponse tariffResultResponse);
    List<TariffResponse> getAllTariffResponses();
}
