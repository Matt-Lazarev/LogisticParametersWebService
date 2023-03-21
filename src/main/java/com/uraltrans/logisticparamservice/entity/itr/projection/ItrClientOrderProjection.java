package com.uraltrans.logisticparamservice.entity.itr.projection;

import java.math.BigDecimal;
import java.sql.Timestamp;

public interface ItrClientOrderProjection {
    String getCarNumber();
    String getDocumentStatus();
    String getCarType();
    Double getVolumeFrom();
    Double getVolumeTo();
    String getClient();
    String getSourceStationCode6();
    String getDestinationStationCode6();
    String getCargo();
    String getCargoCode();
    BigDecimal getCarsAmount();
    String getManager();
    Timestamp getDateFrom();
    Timestamp getDateTo();
    BigDecimal getUtRate();
}
