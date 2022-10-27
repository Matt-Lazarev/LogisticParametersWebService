package com.uraltrans.logisticparamservice.repository.utcsrs;

import java.util.List;
import java.util.Map;

public interface RegisterSecondEmptyFlightRepository {
    boolean containsFlightsByCodes(String sourceStationCode, String destStationCode);
}
