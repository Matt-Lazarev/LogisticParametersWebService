package com.uraltrans.logisticparamservice.service.utcsrs;

import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.repository.utcsrs.RawFlightProfitRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import com.uraltrans.logisticparamservice.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RawFlightProfitServiceImpl implements RawFlightProfitService {
    private final RawFlightProfitRepository rawFlightProfitRepository;
    private final LoadParameterService loadParameterService;

    @Override
    public List<Map<String, Object>> getAll() {
        LoadParameters params = loadParameterService.getLoadParameters();
        Integer daysToRetrieve = params.getFlightProfitDaysToRetrieveData();
        String fromDate = Mapper.to1cDate(LocalDate.now().minusDays(daysToRetrieve)).toString();
        return rawFlightProfitRepository.getAllFlightProfits(fromDate);
    }
}
