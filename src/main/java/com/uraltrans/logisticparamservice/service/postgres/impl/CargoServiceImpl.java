package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.station.CargoResponse;
import com.uraltrans.logisticparamservice.entity.postgres.Cargo;
import com.uraltrans.logisticparamservice.repository.postgres.CargoRepository;
import com.uraltrans.logisticparamservice.repository.utcsrs.RawCargoRepository;
import com.uraltrans.logisticparamservice.service.mapper.CargoMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.CargoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CargoServiceImpl implements CargoService {
    private final CargoRepository cargoRepository;
    private final RawCargoRepository rawCargoRepository;
    private final CargoMapper cargoMapper;

    @Override
    public List<Cargo> getAllCargos() {
        return cargoRepository.findAll();
    }

    @Override
    public List<CargoResponse> getAllCargoResponses() {
        return cargoMapper.mapToCargoResponseList(cargoRepository.findAll());
    }

    @Override
    public void saveAll() {
        prepareNextSave();
        List<Cargo> cargos = cargoMapper.mapRawDataToCargoList(rawCargoRepository.getAllFlightProfits());
        cargos = filterCargos(cargos);
        cargoRepository.saveAll(cargos);
    }

    private void prepareNextSave() {
        cargoRepository.truncate();
    }

    private List<Cargo> filterCargos(List<Cargo> cargos){
        return cargos.stream()
                .filter(c -> c.getCode() != null && c.getName() != null)
                .collect(Collectors.toList());
    }
}
