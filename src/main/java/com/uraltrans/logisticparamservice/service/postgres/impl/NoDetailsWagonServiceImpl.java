package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.NoDetailsWagon;
import com.uraltrans.logisticparamservice.repository.integration.IntegrationCarRepairRepository;
import com.uraltrans.logisticparamservice.repository.itr.ItrDislocationRepository;
import com.uraltrans.logisticparamservice.repository.postgres.NoDetailsWagonRepository;
import com.uraltrans.logisticparamservice.service.mapper.mapstruct.ItrDislocationMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.NoDetailsWagonService;
import com.uraltrans.logisticparamservice.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class NoDetailsWagonServiceImpl implements NoDetailsWagonService {
    private static final String FEATURE2_FILTER_VALUE = "не оформлен";

    private final NoDetailsWagonRepository noDetailsWagonRepository;
    private final ItrDislocationRepository itrDislocationRepository;
    private final IntegrationCarRepairRepository integrationCarRepairRepository;
    private final ItrDislocationMapper itrDislocationMapper;

    @Override
    public List<NoDetailsWagon> getAllNoDetailsWagon() {
        return noDetailsWagonRepository.findAll();
    }

    @Override
    public void saveAllNoDetailsWagon() {
        prepareNextSave();

        List<NoDetailsWagon> noDetailsWagons = itrDislocationMapper.toDislocationList(itrDislocationRepository.getAllItrDislocations());
        noDetailsWagons = filterNoDetailsWagons(noDetailsWagons);
        loadRepairInfo(noDetailsWagons);
        noDetailsWagonRepository.saveAll(noDetailsWagons);
    }

    private void prepareNextSave() {
        noDetailsWagonRepository.truncate();
    }

    private void loadRepairInfo(List<NoDetailsWagon> noDetailsWagons){
        noDetailsWagons
                .forEach(wagon -> {
                    LocalDate current = MappingUtils.fix1cDate(LocalDate.now());
                    integrationCarRepairRepository.getCarRepairByCarNumber(
                            current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), wagon.getCarNumber())
                                    .ifPresentOrElse(
                                            repairInfo -> wagon.setRefurbished(repairInfo.getRefurbished().equals("1")),
                                            ()->wagon.setRefurbished(false));
                });
    }

    private List<NoDetailsWagon> filterNoDetailsWagons(List<NoDetailsWagon> noDetailsWagons) {
        return noDetailsWagons
                .stream()
                .filter(w -> w.getDistanceFromCurrentStation().equals("0.0"))
                .filter(w -> w.getP02().isEmpty() || w.getP02().toLowerCase().contains(FEATURE2_FILTER_VALUE))
                .collect(Collectors.toList());
    }
}
