package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.regionsegmentation.RegionFlightGroupKey;
import com.uraltrans.logisticparamservice.entity.postgres.RegionFlight;
import com.uraltrans.logisticparamservice.entity.postgres.RegionFlightCollapsed;
import com.uraltrans.logisticparamservice.repository.postgres.RegionFlightCollapsedRepository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionFlightCollapsedService;
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
public class RegionFlightCollapsedServiceImpl implements RegionFlightCollapsedService {
    private final RegionFlightService regionFlightService;
    private final RegionSegmentationLogService regionSegmentationLogService;
    private final RegionFlightCollapsedRepository regionFlightCollapsedRepository;

    @Override
    public List<RegionFlightCollapsed> getAllRegionFlightCollapsed() {
        return regionFlightCollapsedRepository.findAll();
    }

    @Override
    public void saveAllRegionFlightsCollapsed(String logId){
        prepareNextSave();

        List<RegionFlight> regionFlights = regionFlightService.getAllRegionFlights();
        regionFlights = filterRegionFlightsByTravelDays(regionFlights, logId);

        List<RegionFlightCollapsed> groupedRegionFlights = groupRegionFlightsByRegion(regionFlights, logId);
        regionFlightCollapsedRepository.saveAll(groupedRegionFlights);
    }

    private void prepareNextSave(){
        regionFlightCollapsedRepository.truncate();
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

    private List<RegionFlightCollapsed> groupRegionFlightsByRegion(List<RegionFlight> regionFlights, String logId){
        List<RegionFlightCollapsed> groupedFlights = regionFlights
                .stream()
                .collect(Collectors.groupingBy(
                        f -> new RegionFlightGroupKey(f.getVolume(), f.getSourceRegion(), f.getDestRegion(), f.getType()),
                        Collectors.toList()))
                .entrySet()
                .stream()
                .collect(ArrayList::new,
                        (resultList, entry) -> resultList.add(RegionFlightCollapsed.builder()
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
