package com.uraltrans.logisticparamservice.service.utcsrs;

import com.uraltrans.logisticparamservice.dto.request.LoadDataRequestDto;
import com.uraltrans.logisticparamservice.repository.utcsrs.RawFlightProfitRepository;
import com.uraltrans.logisticparamservice.utils.EnvUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RawFlightProfitServiceImpl implements RawFlightProfitService {
    private final RawFlightProfitRepository rawFlightProfitRepository;
    private final Environment env;

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAll() {
        LoadDataRequestDto params = EnvUtils.getRequestParams(env);
        Integer daysToRetrieve = params.getFlightProfitDaysToRetrieveData();
        String fromDate = LocalDateTime.now().minusDays(daysToRetrieve).toString();
        return rawFlightProfitRepository.getAllFlightProfits(fromDate);
    }
}
