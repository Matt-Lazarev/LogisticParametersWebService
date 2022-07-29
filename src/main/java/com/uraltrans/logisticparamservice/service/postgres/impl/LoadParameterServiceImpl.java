package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.repository.postgres.LoadParametersRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    @Transactional(readOnly = true)
    public LocalTime getNextDataLoadTime() {
        LoadParameters parameters = getOrDefault();
        String time = parameters.getNextDataLoadTime();
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getManagers() {
        return Arrays
                .stream(getOrDefault().getManagers().split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private LoadParameters getOrDefault(){
        List<LoadParameters> loadParameters = loadParametersRepository.findAll();
        if(loadParameters.size() != 0){
            return loadParameters.get(0);
        }

        LoadParameters parameters = new LoadParameters(
                1, "09:45:00",
                20, 20,
                0, 0,
                60., 1.,
                45, "Согласован",
                "КР", "Мельниченко Д.В., Чаброва М.С., Зырина Н.А., Холмогорова Е.А., Мелешкина Ю.В., Афанасьева А.Л., Субботин В.В., Артеменко М.Н., Коркишко В.В.");
        loadParametersRepository.save(parameters);
        return parameters;
    }
}
