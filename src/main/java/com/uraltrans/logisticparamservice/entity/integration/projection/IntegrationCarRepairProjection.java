package com.uraltrans.logisticparamservice.entity.integration.projection;

import java.math.BigDecimal;

public interface IntegrationCarRepairProjection {
    String getCarNumber();
    String getNonworkingPark();
    String getRefurbished();
    String getRejected();
    String getRequiresRepair();
    BigDecimal getThicknessWheel();
    BigDecimal getThicknessComb();
}
