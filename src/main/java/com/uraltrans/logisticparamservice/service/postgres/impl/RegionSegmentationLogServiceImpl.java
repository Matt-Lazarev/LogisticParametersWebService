package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationLog;
import com.uraltrans.logisticparamservice.repository.postgres.RegionSegmentationLogRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionSegmentationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RegionSegmentationLogServiceImpl implements RegionSegmentationLogService {
    private final RegionSegmentationLogRepository regionSegmentationLogRepository;

    @Override
    public List<RegionSegmentationLog> getAllLogs() {
        return regionSegmentationLogRepository.findAll();
    }

    @Override
    public Optional<RegionSegmentationLog> getLogById(String id) {
        return regionSegmentationLogRepository.findById(id);
    }

    @Override
    public String saveLog(){
        RegionSegmentationLog log = new RegionSegmentationLog();
        log.setUuid(UUID.randomUUID().toString());
        log.setMessage("");
        return regionSegmentationLogRepository.save(log).getUuid();
    }

    @Override
    public void updateLogMessageById(String id, List<String> messages){
        RegionSegmentationLog log = regionSegmentationLogRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException(String.format("Log with id='%d' not found", id)));

        messages = addTimestampToMessages(messages);
        log.setMessage(log.getMessage() + "\n" + String.join("\n", messages));
        regionSegmentationLogRepository.save(log);
    }

    @Override
    public void updateLogMessageById(String id, String message){
        this.updateLogMessageById(id, Collections.singletonList(message));
    }

    private List<String> addTimestampToMessages(List<String> messages) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH.mm.ss");
        return messages
                .stream()
                .map(m -> LocalDateTime.now().format(formatter) + "    " + m)
                .collect(Collectors.toList());
    }
}
