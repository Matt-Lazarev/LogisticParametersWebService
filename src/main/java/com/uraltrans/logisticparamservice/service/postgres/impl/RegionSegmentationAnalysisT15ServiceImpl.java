package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.regionsegmentation.RegionFlightGroupKey;
import com.uraltrans.logisticparamservice.entity.postgres.RegionFlight;
import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationAnalysisT15;
import com.uraltrans.logisticparamservice.repository.postgres.RegionSegmentationAnalysisT15Repository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionSegmentationAnalysisT15Service;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionFlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionSegmentationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionSegmentationAnalysisT15ServiceImpl implements RegionSegmentationAnalysisT15Service {
    private final RegionFlightService regionFlightService;
    private final RegionSegmentationLogService regionSegmentationLogService;
    private final RegionSegmentationAnalysisT15Repository regionSegmentationAnalysisT15Repository;

    @Override
    public List<RegionSegmentationAnalysisT15> getAllT15Analyses() {
        return regionSegmentationAnalysisT15Repository.findAll();
    }

    @Override
    public void saveAllRegionSegmentationsAnalysisT15(String logId){
        prepareNextSave();

        List<RegionFlight> regionFlights = regionFlightService.getAllRegionFlights();
        regionFlights = filterRegionFlightsByTravelDays(regionFlights, logId);

        List<RegionSegmentationAnalysisT15> groupedRegionFlights = groupRegionFlightsByRegion(regionFlights, logId);
        regionSegmentationAnalysisT15Repository.saveAll(groupedRegionFlights);
    }

    private void prepareNextSave(){
        regionSegmentationAnalysisT15Repository.truncate();
    }

    private List<RegionFlight> filterRegionFlightsByTravelDays(List<RegionFlight> regionFlights, String logId){
        List<RegionFlight> filteredFlights = regionFlights
                .stream()
                .filter(f -> f.getTravelDays() != null)
                .collect(Collectors.toList());

        long loadedCount = filteredFlights.stream().filter(f -> f.getType().equals("Груженый")).count();
        long emptyCount = filteredFlights.stream().filter(f -> f.getType().equals("Порожний")).count();

        String message1 = String.format("[Фильтр по времени перевозки]: Груженых рейсов = %d", loadedCount);
        String message2 = String.format("[Фильтр по времени перевозки]: Порожних рейсов = %d", emptyCount);
        regionSegmentationLogService.updateLogMessageById(logId, Arrays.asList(message1, message2));
        return filteredFlights;
    }

    private List<RegionSegmentationAnalysisT15> groupRegionFlightsByRegion(List<RegionFlight> regionFlights, String logId){
        List<RegionSegmentationAnalysisT15> groupedFlights = regionFlights
                .stream()
                .collect(Collectors.groupingBy(
                        f -> new RegionFlightGroupKey(f.getVolume(), f.getSourceRegion(), f.getDestRegion(), f.getType()),
                        Collectors.toList()))
                .entrySet()
                .stream()
                .collect(ArrayList::new,
                        (resultList, entry) -> resultList.add(RegionSegmentationAnalysisT15.builder()
                                .volume(entry.getKey().getVolume())
                                .sourceRegion(entry.getKey().getSourceRegion())
                                .destRegion(entry.getKey().getDestRegion())
                                .type(entry.getKey().getType())
                                .rateTariff(BigDecimal.valueOf(entry.getValue().stream().mapToDouble(f -> f.getRateTariff().doubleValue()).sum()))
                                .flightsAmount(entry.getValue().stream().mapToInt(RegionFlight::getFlightsAmount).sum())
                                .travelDays(entry.getValue().stream().mapToInt(RegionFlight::getTravelDays).sum())
                                .sourceRegionLoadIdleDays(entry.getValue().stream().mapToInt(RegionFlight::getSourceStationLoadIdleDays).sum())
                                .sourceRegionUnloadIdleDays(entry.getValue().stream().mapToInt(RegionFlight::getSourceStationUnloadIdleDays).sum())
                                .destRegionLoadIdleDays(entry.getValue().stream().mapToInt(RegionFlight::getDestStationLoadIdleDays).sum())
                                .destRegionUnloadIdleDays(entry.getValue().stream().mapToInt(RegionFlight::getDestStationUnloadIdleDays).sum())
                                .build()),
                        ArrayList::addAll
                );

        long loadedCount = groupedFlights.stream().filter(f -> f.getType().equals("Груженый")).count();
        long emptyCount = groupedFlights.stream().filter(f -> f.getType().equals("Порожний")).count();

        String message1 = String.format("[Группировка по региону]: Груженых рейсов = %d", loadedCount);
        String message2 = String.format("[Группировка по региону]: Порожних рейсов = %d", emptyCount);
        regionSegmentationLogService.updateLogMessageById(logId, Arrays.asList(message1, message2));

        return groupedFlights;
    }
}
