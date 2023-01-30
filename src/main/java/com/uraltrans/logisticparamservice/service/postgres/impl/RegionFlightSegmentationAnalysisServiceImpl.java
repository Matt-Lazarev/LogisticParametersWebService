package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.regionsegmentation.RegionFlightGroupKey;
import com.uraltrans.logisticparamservice.entity.postgres.RegionFlight;
import com.uraltrans.logisticparamservice.entity.postgres.RegionFlightSegmentationAnalysisT15;
import com.uraltrans.logisticparamservice.repository.postgres.RegionFlightSegmentationAnalysisT15Repository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionFlightSegmentationAnalysisT15Service;
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
public class RegionFlightSegmentationAnalysisServiceImpl implements RegionFlightSegmentationAnalysisT15Service {
    private final RegionFlightService regionFlightService;
    private final RegionSegmentationLogService regionSegmentationLogService;
    private final RegionFlightSegmentationAnalysisT15Repository regionFlightSegmentationAnalysisT15Repository;

    @Override
    public List<RegionFlightSegmentationAnalysisT15> getAllT15Analyses() {
        return regionFlightSegmentationAnalysisT15Repository.findAll();
    }

    @Override
    public void saveAllRegionSegmentationsAnalysisT15(String logId){
        prepareNextSave();

        List<RegionFlight> regionFlights = regionFlightService.getAllRegionFlights();
        regionFlights = filterRegionFlightsByTravelDays(regionFlights, logId);

        List<RegionFlightSegmentationAnalysisT15> groupedRegionFlights = groupRegionFlightsByRegion(regionFlights, logId);
        regionFlightSegmentationAnalysisT15Repository.saveAll(groupedRegionFlights);
    }

    private void prepareNextSave(){
        regionFlightSegmentationAnalysisT15Repository.truncate();
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

    private List<RegionFlightSegmentationAnalysisT15> groupRegionFlightsByRegion(List<RegionFlight> regionFlights, String logId){
        List<RegionFlightSegmentationAnalysisT15> groupedFlights = regionFlights
                .stream()
                .collect(Collectors.groupingBy(
                        f -> new RegionFlightGroupKey(f.getVolume(), f.getSourceRegion(), f.getDestRegion(), f.getType()),
                        Collectors.toList()))
                .entrySet()
                .stream()
                .collect(ArrayList::new,
                        (resultList, entry) -> resultList.add(RegionFlightSegmentationAnalysisT15.builder()
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
