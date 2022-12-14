package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.repository.postgres.LoadParametersRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public LoadParameters getLoadParameters() {
        return getOrDefault();
    }

    @Override
    public void updateLoadParameters(LoadParameters newParameters) {
        LoadParameters loadParameters = loadParametersRepository.findAll().get(0);
        newParameters.setId(loadParameters.getId());
        if(newParameters.getRateTariffState() == null){
            newParameters.setRateTariffState(loadParameters.getRateTariffState());
        }
        loadParametersRepository.save(newParameters);
    }

    @Override
    public LocalTime getNextDataLoadTime() {
        LoadParameters parameters = getOrDefault();
        String time = parameters.getNextDataLoadTime();
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    @Override
    public void updateNextDataLoad(LocalDateTime nextExecution) {
        LoadParameters parameters = getOrDefault();
        parameters.setNextDataLoadTime(nextExecution.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        loadParametersRepository.save(parameters);
    }

    @Override
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
                90, "03:00:00",
                30, 30,
                0, 0,
                60., 1.,
                45, "Согласован",
                "КР", "Мельниченко Д.В., Чаброва М.С., Зырина Н.А., Холмогорова Е.А., Мелешкина Ю.В., Афанасьева А.Л., Субботин В.В., Артеменко М.Н., Коркишко В.В., Варфоломеев В.., Панов С.Н.",
                "791b94ee-cac3-44b3-896e-d31264886a32", "d2290efc-9926-474b-ba3e-854bb0b9c7e6", 950, "воин, аренда, вояк", "воин, аренда, вояк", 7, "", "УРАЛЬСКАЯ ТРАНСПОРТНАЯ КОМПАНИЯ");
        loadParametersRepository.save(parameters);
        return parameters;
    }
}
