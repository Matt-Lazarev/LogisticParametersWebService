package com.uraltrans.logisticparamservice.repository.utcsrs;


public interface RegisterSecondEmptyFlightRepository {
    boolean containsFlightsByCodes(String sourceStationCode, String destStationCode);
}
