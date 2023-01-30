package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationLog;

import java.util.List;
import java.util.Optional;

public interface RegionSegmentationLogService {

     List<RegionSegmentationLog> getAllLogs();

     Optional<RegionSegmentationLog> getLogById(String id);

     String saveLog();

     void updateLogMessageById(String id, List<String> messages);

     void updateLogMessageById(String id, String message);
}
