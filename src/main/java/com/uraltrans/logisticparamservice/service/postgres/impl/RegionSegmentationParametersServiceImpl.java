package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationParameters;
import com.uraltrans.logisticparamservice.repository.postgres.RegionSegmentationParametersRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionSegmentationParametersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionSegmentationParametersServiceImpl implements RegionSegmentationParametersService {
    private final RegionSegmentationParametersRepository regionSegmentationParametersRepository;

    @Override
    public RegionSegmentationParameters getParameters() {
        List<RegionSegmentationParameters> list = regionSegmentationParametersRepository.findAll();
        if(list.size() == 1){
            return list.get(0);
        }

        return getDefaultParameters();
    }

    @Override
    public void updateParameters(RegionSegmentationParameters newParameters) {
        List<RegionSegmentationParameters> list = regionSegmentationParametersRepository.findAll();
        if(list.size() != 0){
            newParameters.setId(list.get(0).getId());
        }
        regionSegmentationParametersRepository.save(newParameters);
    }

    private RegionSegmentationParameters getDefaultParameters() {
        return new RegionSegmentationParameters(null, "", "УРАЛЬСКАЯ ТРАНСПОРТНАЯ КОМПАНИЯ",
                1, 1, 45,
                60, 8, 5, 3,
                "", "t2.csv", "t3.csv",
                "05:00:00");
    }
}
