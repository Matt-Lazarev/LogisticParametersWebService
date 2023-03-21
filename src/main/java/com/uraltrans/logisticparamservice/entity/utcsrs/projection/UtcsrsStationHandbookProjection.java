package com.uraltrans.logisticparamservice.entity.utcsrs.projection;

public interface UtcsrsStationHandbookProjection {
     String getCode();
     String getDescription();
     String getRegion();
     String getRoad();
     String getLatitude();
     String getLongitude();
     String getExcludeFromSecondEmptyFlight();
     String getLock();
}
