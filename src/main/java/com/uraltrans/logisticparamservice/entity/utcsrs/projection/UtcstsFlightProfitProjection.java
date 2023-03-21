package com.uraltrans.logisticparamservice.entity.utcsrs.projection;

import java.math.BigDecimal;
import java.sql.Timestamp;

public interface UtcstsFlightProfitProjection {
     String getSourceStationCode();
     String getDestStationCode();
     String getCargoCode();
     String getCargo();
     BigDecimal getProfit();
     String getCurrency();
     BigDecimal getVolume();
     Timestamp getSendDate();
}
