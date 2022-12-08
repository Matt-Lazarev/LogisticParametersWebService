package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.nodetailswagon.NoDetailsWagon;
import com.uraltrans.logisticparamservice.repository.integration.CarRepairInfoRepository;
import com.uraltrans.logisticparamservice.repository.integration.RawDislocationRepository;
import com.uraltrans.logisticparamservice.service.mapper.NoDetailsWagonMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.NoDetailsWagonService;
import com.uraltrans.logisticparamservice.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.uraltrans.logisticparamservice.utils.Mapper.SHIFT_1C_YEARS;

@Service
@RequiredArgsConstructor
public class NoDetailsWagonServiceImpl implements NoDetailsWagonService {
    private static final String FEATURE2_FILTER_VALUE = "не оформлен";
    private final RawDislocationRepository dislocationRepository;
    private final CarRepairInfoRepository carRepairInfoRepository;
    private final NoDetailsWagonMapper noDetailsWagonMapper;

    @Override
    public List<NoDetailsWagon> getAllNoDetailsWagon() {
        String dislocationDate = LocalDate.now().plusYears(SHIFT_1C_YEARS).toString();
        List<NoDetailsWagon> noDetailsWagons = noDetailsWagonMapper.mapToNoDetailsWagons(dislocationRepository.getAllDislocations(dislocationDate));
        loadRepairInfo(noDetailsWagons);
        noDetailsWagons = filterNoDetailsWagons(noDetailsWagons);
        return noDetailsWagons;
    }

    private void loadRepairInfo(List<NoDetailsWagon> noDetailsWagons){
        noDetailsWagons
                .forEach(wagon -> {
                    LocalDate current = Mapper.fix1cDate(LocalDate.now());
                    Map<String, Object> repairInfo = carRepairInfoRepository.getCarRepairByDate(
                            current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), Integer.parseInt(wagon.getCarNumber()));
                    wagon.setRefurbished(!repairInfo.isEmpty() && ((byte[]) repairInfo.get("Refurbished"))[0] == 1);
                });
    }

    private List<NoDetailsWagon> filterNoDetailsWagons(List<NoDetailsWagon> noDetailsWagons) {
        return noDetailsWagons
                .stream()
                .filter(w -> w.getDistanceFromCurrentStation().equals("0"))
                .filter(w ->w.getP02().isEmpty() || w.getP02().toLowerCase().contains(FEATURE2_FILTER_VALUE))
                .collect(Collectors.toList());
    }
}