package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.*;
import com.uraltrans.logisticparamservice.repository.postgres.ProfitThresholdT7Repository;
import com.uraltrans.logisticparamservice.repository.postgres.RegionSegmentationT15Repository;
import com.uraltrans.logisticparamservice.repository.postgres.SegmentationResultT15Repository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionSegmentationAnalysisT15Service;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionSegmentationLogService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionSegmentationParametersService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.SegmentationResultT15Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SegmentationResultT15ServiceImpl implements SegmentationResultT15Service {
    private final RegionSegmentationT15Repository regionSegmentationT15Repository;
    private final ProfitThresholdT7Repository profitThresholdT7Repository;
    private final SegmentationResultT15Repository segmentationResultT15Repository;
    private final RegionSegmentationAnalysisT15Service regionSegmentationAnalysisT15Service;
    private final RegionSegmentationParametersService regionSegmentationParametersService;
    private final RegionSegmentationLogService regionSegmentationLogService;

    @Override
    public void saveAllSegments(String logId) {
        prepareNextSave();

        RegionSegmentationParameters parameters = regionSegmentationParametersService.getParameters();
        Integer maxSegments = parameters.getMaxSegments();

        List<RegionSegmentationAnalysisT15> flights = regionSegmentationAnalysisT15Service.getAllT15Analyses();

        Map<Integer, RegionSegmentationAnalysisT15> loadedFlights = getFlightsByType(flights, "Груженый");
        Map<Integer, RegionSegmentationAnalysisT15> emptyFlights = getFlightsByType(flights, "Порожний");

        Map<String, List<RegionSegmentationAnalysisT15>> emptyFlightsGroupedBySourceRegion = groupBySourceRegion(emptyFlights.values());
        Map<String, List<RegionSegmentationAnalysisT15>> loadedFlightsGroupedBySourceRegion = groupBySourceRegion(loadedFlights.values());

        List<List<RegionSegmentationAnalysisT15>> segmentations = getFirstSegmentation(loadedFlights, emptyFlightsGroupedBySourceRegion, logId);

        String message = String.format("[Расчет сегментации]: Найдено %d сегментов уровня 1", segmentations.size());
        regionSegmentationLogService.updateLogMessageById(logId, message);


        for (int i = 1; i < maxSegments; i++) {
            fillNextSegmentPart(segmentations, loadedFlightsGroupedBySourceRegion, logId);
            fillNextSegmentPart(segmentations, emptyFlightsGroupedBySourceRegion, logId);

            message = String.format("[Расчет сегментации]: Найдено %d сегментов уровня %d", segmentations.size(), i+1);
            regionSegmentationLogService.updateLogMessageById(logId, message);
        }

        List<RegionSegmentationT15> finalSegmentations = mapSegmentsListToSegmentation(segmentations);
        regionSegmentationT15Repository.saveAll(finalSegmentations);

        List<SegmentationResultT15> segmentationResult = mapToSegmentationResult(finalSegmentations);
        segmentationResultT15Repository.saveAll(segmentationResult);

        regionSegmentationLogService.updateLogMessageById(logId, "Сегментация завершена");
    }

    @Override
    public List<SegmentationResultT15> getAllSegmentations(){
        return segmentationResultT15Repository.findAll();
    }

    private void prepareNextSave() {
        regionSegmentationT15Repository.truncateT15Table();
        segmentationResultT15Repository.truncate();
    }

    private Map<Integer, RegionSegmentationAnalysisT15> getFlightsByType(List<RegionSegmentationAnalysisT15> flights, String type) {
        return flights
                .stream()
                .filter(f -> f.getType().equals(type))
                .collect(Collectors.toMap(RegionSegmentationAnalysisT15::getId, Function.identity()));
    }

    private Map<String, List<RegionSegmentationAnalysisT15>> groupBySourceRegion(Collection<RegionSegmentationAnalysisT15> flights){
        return flights
                .stream()
                .filter(f -> !f.getSourceRegion().equals(f.getDestRegion()))
                .collect(Collectors.groupingBy(rf -> rf.getSourceRegion() + rf.getVolume(), Collectors.toList()));
    }

    private List<List<RegionSegmentationAnalysisT15>> getFirstSegmentation(
            Map<Integer, RegionSegmentationAnalysisT15> loadedFlights,
            Map<String, List<RegionSegmentationAnalysisT15>> emptyFlightsGroupedBySourceRegion, String logId) {
        List<List<RegionSegmentationAnalysisT15>> currentSegmentation = new ArrayList<>();
        for (RegionSegmentationAnalysisT15 loaded : loadedFlights.values()) {
            List<RegionSegmentationAnalysisT15> empties = emptyFlightsGroupedBySourceRegion.get(loaded.getDestRegion() + loaded.getVolume());
            if (empties != null) {
                for (RegionSegmentationAnalysisT15 empty : empties) {
                    List<RegionSegmentationAnalysisT15> segment = new ArrayList<>(Arrays.asList(loaded, empty));
                    currentSegmentation.add(segment);
                }
            } else {
                String message = String.format("[Расчет сегментации]: Не удалось найти порожние рейсы от региона: = %s", loaded.getDestRegion());
                regionSegmentationLogService.updateLogMessageById(logId, message);
            }
        }
        return currentSegmentation;
    }

    private void fillNextSegmentPart(
            List<List<RegionSegmentationAnalysisT15>> segmentations,
            Map<String, List<RegionSegmentationAnalysisT15>> flightsGroupedBySourceRegion, String logId) {

        List<List<RegionSegmentationAnalysisT15>> newSegmentations = new ArrayList<>();
        for (List<RegionSegmentationAnalysisT15> segmentation : segmentations) {
            RegionSegmentationAnalysisT15 lastSegmentPart = segmentation.get(segmentation.size() - 1);

            if (segmentation.size() % 2 == 0 && lastSegmentPart.getIsNextSegmentNofFound() != null && lastSegmentPart.getIsNextSegmentNofFound()) {
                continue;
            }

            List<RegionSegmentationAnalysisT15> nextSegmentPartFlights = flightsGroupedBySourceRegion.get(lastSegmentPart.getDestRegion() + lastSegmentPart.getVolume());
            if (nextSegmentPartFlights != null) {
                List<RegionSegmentationAnalysisT15> segmentationCopy = new ArrayList<>(segmentation);

                RegionSegmentationAnalysisT15 firstFlight = nextSegmentPartFlights.get(0);
                segmentation.add(firstFlight);

                for (int i = 1; i < nextSegmentPartFlights.size(); i++) {
                    RegionSegmentationAnalysisT15 nextSegmentPart = nextSegmentPartFlights.get(i);
                    List<RegionSegmentationAnalysisT15> newSegmentation = new ArrayList<>(segmentationCopy);
                    newSegmentation.add(nextSegmentPart);
                    newSegmentations.add(newSegmentation);
                }
            } else {
                String message;
                if (segmentation.size() % 2 == 1) {
                    segmentation.remove(segmentation.size() - 1);
                    segmentation.get(segmentation.size() - 1).setIsNextSegmentNofFound(true);
                    message = "[Расчет сегментации]: Не удалось найти порожние рейсы от региона: %s";
                } else {
                    lastSegmentPart.setIsNextSegmentNofFound(true);
                    message = "[Расчет сегментации]: Не удалось найти груженые рейсы от региона: %s";
                }

                regionSegmentationLogService.updateLogMessageById(logId, String.format(message, lastSegmentPart.getDestRegion()));
            }
        }
        segmentations.addAll(newSegmentations);
    }

    public List<RegionSegmentationT15> mapSegmentsListToSegmentation(List<List<RegionSegmentationAnalysisT15>> segmentList) {
        List<RegionSegmentationT15> segmentations = new ArrayList<>();
        for (List<RegionSegmentationAnalysisT15> segmentationAnalysis : segmentList) {
            RegionSegmentationT15 finalSegmentation = new RegionSegmentationT15();
            List<RegionSegment> segments = new ArrayList<>();
            for (int i = 0; i < segmentationAnalysis.size(); i += 2) {
                RegionSegmentationAnalysisT15 loaded = segmentationAnalysis.get(i);
                RegionSegmentationAnalysisT15 empty = segmentationAnalysis.get(i + 1);

                List<RegionSegmentationAnalysisT15> segmentation = Arrays.asList(loaded, empty);
                BigDecimal profit = calculateProfit(segmentation);
                Integer travelDays = loaded.getTravelDays() + empty.getTravelDays();
                Integer loadDays = loaded.getSourceRegionLoadIdleDays();
                Integer unloadDays = loaded.getDestRegionUnloadIdleDays();

                RegionSegment segment = new RegionSegment(loaded.getSourceRegion(), loaded.getDestRegion(),
                        empty.getSourceRegion(), empty.getDestRegion(), profit, travelDays, loadDays, unloadDays);
                segments.add(segment);
            }

            finalSegmentation.setVolume(segmentationAnalysis.get(0).getVolume());
            finalSegmentation.setSegments(segments);
            setSegmentType(finalSegmentation);

            segmentations.add(finalSegmentation);
        }
        return segmentations;
    }

    private BigDecimal calculateProfit(List<RegionSegmentationAnalysisT15> segmentation) {
        RegionSegmentationAnalysisT15 loaded = segmentation.get(0);
        RegionSegmentationAnalysisT15 empty = segmentation.get(1);

        BigDecimal loadedAverageRate = loaded.getRateTariff().divide(new BigDecimal(loaded.getFlightsAmount()), 2, RoundingMode.HALF_UP);
        BigDecimal emptyAverageTariff = empty.getRateTariff().divide(new BigDecimal(empty.getFlightsAmount()), 2, RoundingMode.HALF_UP);
        int totalSegmentDays = loaded.getSourceRegionLoadIdleDays() + loaded.getDestRegionUnloadIdleDays() + loaded.getTravelDays() + empty.getTravelDays();

        return (loadedAverageRate.subtract(emptyAverageTariff))
                .divide(new BigDecimal(totalSegmentDays), 2, RoundingMode.HALF_UP);
    }

    private void setSegmentType(RegionSegmentationT15 segmentation) {
        RegionSegmentationParameters parameters = regionSegmentationParametersService.getParameters();
        int maxSegments = parameters.getMaxSegments();

        List<RegionSegment> segments = segmentation.getSegments();
        boolean isCyclicSegmentation = isCyclicSegmentation(segments);
        if (isCyclicSegmentation) {
            segmentation.setSegmentType("Цикл");
            return;
        }

        ProfitThresholdT7 profitThreshold = profitThresholdT7Repository.findByVolume(segmentation.getVolume().intValue());

        boolean containsProfitable = false;
        boolean containsOptimization = false;

        for (int i = 0; i < segments.size(); i++) {
            if (segments.get(i).getProfit().doubleValue() >= profitThreshold.getCost()) {
                containsProfitable = true;
            } else {
                containsOptimization = true;
            }

            if (containsProfitable && containsOptimization) {
                segmentation.setSegments(segments.subList(0, i+1));
                segmentation.setSegmentType("Смена доходный / оптимизационный");
                return;
            }
        }

        if (!containsOptimization && segments.size() == maxSegments) {
            segmentation.setSegmentType("Доходный");
        }
        else if (!containsProfitable && segments.size() == maxSegments) {
            segmentation.setSegmentType("Оптимизационный");
        }
        else {
            segmentation.setSegmentType("Продолжение не найдено");
        }
    }

    private boolean isCyclicSegmentation(List<RegionSegment> segments) {
        RegionSegment firstPart = segments.get(0);
        RegionSegment lastPart = segments.get(segments.size() - 1);
        return firstPart.getLoadedSourceRegion().equals(lastPart.getEmptyDestRegion());
    }

    private List<SegmentationResultT15> mapToSegmentationResult(List<RegionSegmentationT15> segmentations){
        List<SegmentationResultT15> segmentationResults = new ArrayList<>();
        for (RegionSegmentationT15 segmentation : segmentations){
            List<RegionSegment> segments = segmentation.getSegments();
            SegmentationResultT15 segmentationResult = new SegmentationResultT15();
            Integer wholeDays = segments.stream().mapToInt(s -> s.getLoadIdleDays() + s.getUnloadIdleDays() + s.getSegmentTravelDays()).sum();
            Integer wholeTravelDays = segments.stream().mapToInt(RegionSegment::getSegmentTravelDays).sum();
            Integer wholeLoadDays = segments.stream().mapToInt(RegionSegment::getLoadIdleDays).sum();
            Integer wholeUnloadDays = segments.stream().mapToInt(RegionSegment::getUnloadIdleDays).sum();
            BigDecimal wholeProfit = segments.stream().map(RegionSegment::getProfit).reduce(new BigDecimal(1), BigDecimal::add);
            String path = segments.stream().map(RegionSegment::getPath).collect(Collectors.joining());

            segmentationResult.setSourceRegion(segments.get(0).getLoadedSourceRegion());
            segmentationResult.setDestRegion(segments.get(segments.size()-1).getEmptyDestRegion());
            segmentationResult.setVolume(segmentation.getVolume());
            segmentationResult.setSegmentType(segmentation.getSegmentType());
            segmentationResult.setPath(path);
            segmentationResult.setTotalDays(wholeDays);
            segmentationResult.setTotalTravelDays(wholeTravelDays);
            segmentationResult.setTotalLoadDays(wholeLoadDays);
            segmentationResult.setTotalUnloadDays(wholeUnloadDays);
            segmentationResult.setTotalProfit(wholeProfit);

            segmentationResult.setFirstSegment(segments.get(0).getPath());
            segmentationResult.setFirstProfit(segments.get(0).getProfit());

            if(segments.size() > 1){
                segmentationResult.setSecondSegment(segments.get(1).getPath());
                segmentationResult.setSecondProfit(segments.get(1).getProfit());
            }

            if(segments.size() > 2){
                segmentationResult.setThirdSegment(segments.get(2).getPath());
                segmentationResult.setThirdProfit(segments.get(2).getProfit());
            }

            segmentationResults.add(segmentationResult);
        }

        return segmentationResults;
    }
}
