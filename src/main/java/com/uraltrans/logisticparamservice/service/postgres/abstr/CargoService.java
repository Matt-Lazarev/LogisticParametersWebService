package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.dto.station.CargoResponse;
import com.uraltrans.logisticparamservice.entity.postgres.Cargo;

import java.util.List;

public interface CargoService {
    List<Cargo> getAllCargos();
    List<CargoResponse> getAllCargoResponses();
    void saveAll();
}
