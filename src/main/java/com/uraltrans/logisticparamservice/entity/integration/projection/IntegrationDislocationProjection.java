package com.uraltrans.logisticparamservice.entity.integration.projection;

import java.math.BigDecimal;
import java.sql.Timestamp;

public interface IntegrationDislocationProjection {
    String getCarNumber();
    BigDecimal getVolume();
    String getDislocationStationCode();
    String getSourceStationCode();
    String getSourceStation();
    String getSourceStationRoad();
    String getDestinationStationCode();
    String getDestinationStation();
    String getDestinationStationRoad();
    Timestamp getSendDate();
    String getFeature2();
    String getFeature6();
    String getFeature9();
    String getFeature12();
    String getFeature20();
    String getClientNextTask();
    String getManagerNextTask();
    Timestamp getOperationDateTime();
    String getCarState();
    String getFleetState();
    Timestamp getCurrentOrderBegin();
    Timestamp getCurrentOrderEnd();
    String getLoaded();
    String getWagonType();
    String getCargo();
    String getCargoCode();
    String getOwner();
    String getOperation();
    String getModel();
    String getNextStation();
    BigDecimal getDaysBeforeDatePlanRepair();
    BigDecimal getDistanceFromCurrentStation();
    BigDecimal getRestRun();
    String getIdleDislocationStation();
}
