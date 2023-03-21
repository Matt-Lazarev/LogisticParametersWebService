package com.uraltrans.logisticparamservice.entity.itr.projection;


public interface ItrDislocationProjection {
    String getCarNumber();
    String getVolume();
    String getDislocationStation();
    String getDepartureStation();
    String getDepartureStationName();
    String getDepartureRoadName();
    String getDestinationStation();
    String getDestinationStationName();
    String getDestinationRoadName();
    String getDepartureDate();
    String getP02();
    String getP06();
    String getP09();
    String getP12();
    String getP20();
    String getStatusWagon();
    String getWagonType();
    String getFlightType();
    String getCargoId();
    String getDaysBeforeDatePlanRepair();
    String getDistanceFromCurrentStation();
    String getRestRun();
    String getIdleDislocationStation();
}
