package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.repository.postgres.LoadParametersRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoadParameterServiceImpl implements LoadParameterService  {
    private final LoadParametersRepository loadParametersRepository;

    @Override
    @Transactional(readOnly = true)
    public LoadParameters getLoadParameters() {
        return getOrDefault();
    }

    @Override
    @Transactional
    public void updateLoadParameters(LoadParameters newParameters) {
        LoadParameters loadParameters = loadParametersRepository.findAll().get(0);
        loadParameters.setDaysToRetrieveData(newParameters.getDaysToRetrieveData());
        loadParameters.setNextDataLoadTime(newParameters.getNextDataLoadTime());
        loadParameters.setMaxLoadIdleDays(newParameters.getMaxLoadIdleDays());
        loadParameters.setMaxUnloadIdleDays(newParameters.getMaxUnloadIdleDays());
        loadParameters.setMinLoadIdleDays(newParameters.getMinLoadIdleDays());
        loadParameters.setMinUnloadIdleDays(newParameters.getMinUnloadIdleDays());
        loadParameters.setMaxTravelTime(newParameters.getMaxTravelTime());
        loadParameters.setMinTravelTime(newParameters.getMinTravelTime());
        loadParameters.setFlightProfitDaysToRetrieveData(newParameters.getFlightProfitDaysToRetrieveData());
        loadParameters.setStatus(newParameters.getStatus());
        loadParameters.setCarType(newParameters.getCarType());
        loadParameters.setManagers(newParameters.getManagers());
        loadParametersRepository.save(loadParameters);
    }

    @Override
    @Transactional
    public LocalTime getNextDataLoadTime() {
        LoadParameters parameters = getOrDefault();
        String time = parameters.getNextDataLoadTime();
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getManagers() {
        return null;
    }

    private LoadParameters getOrDefault(){
        List<LoadParameters> loadParameters = loadParametersRepository.findAll();
        if(loadParameters.size() != 0){
            return loadParameters.get(0);
        }

        LoadParameters parameters = new LoadParameters(
                90, "05:00:00",
                20, 20,
                0, 0,
                60., 1.,
                45, "Согласован",
                "КР", "");
        loadParametersRepository.save(parameters);
        return parameters;
    }
}
