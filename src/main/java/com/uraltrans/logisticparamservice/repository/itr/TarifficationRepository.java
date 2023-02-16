package com.uraltrans.logisticparamservice.repository.itr;


import java.util.List;
import java.util.Map;

public interface TarifficationRepository {
    List<Map<String, Object>> getAllTariffications(String dateFrom, String dateTo);

    List<Map<String, Object>> getAllDistances(String tarifficationIds);

    List<Map<String, Object>> getAllSourceStations(String tarifficationIds);

    List<Map<String, Object>> getAllDestStations(String tarifficationIds);

    List<Map<String, Object>> getAllForeignStations(Integer tarifficationId);

    List<Map<String, Object>> getAllExcludedStations(String tarifficationIds);

    List<Map<String, Object>> getAllIncludedStations(String tarifficationIds);

    List<Map<String, Object>> getAllAdditionalColumns1(Integer tarifficationIds);

    List<Map<String, Object>> getAllAdditionalColumns2(Integer parentDocumentId);

    List<Map<String, Object>> getAllAdditionalColumns3(Integer argId, Integer docId);
}
