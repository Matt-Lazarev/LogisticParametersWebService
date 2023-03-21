package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.excelt14.FlightInfo;
import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.entity.postgres.ProfitThresholdT7;
import com.uraltrans.logisticparamservice.entity.postgres.RegionSegment;
import com.uraltrans.logisticparamservice.entity.postgres.RegionFlightCollapsed;
import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationParameters;
import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationT15;
import com.uraltrans.logisticparamservice.entity.postgres.SegmentationResultT15;
import com.uraltrans.logisticparamservice.entity.postgres.StationHandbook;
import com.uraltrans.logisticparamservice.repository.postgres.ProfitThresholdT7Repository;
import com.uraltrans.logisticparamservice.repository.postgres.RegionSegmentationT15Repository;
import com.uraltrans.logisticparamservice.repository.postgres.SegmentationResultT15Repository;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionFlightCollapsedService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionSegmentationLogService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionSegmentationParametersService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.SegmentationResultT15Service;
import com.uraltrans.logisticparamservice.service.postgres.abstr.StationHandbookService;
import com.uraltrans.logisticparamservice.utils.ExcelUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SegmentationResultT15ServiceImpl implements SegmentationResultT15Service {
    private static final List<String> EXCEL_FILE_HEADERS = Arrays.asList(
            "Месяц год отправления", "Станция отправления Код6", "Кол-во вагоноотправок", "Вагоноотправки УТК");

    private final RegionSegmentationT15Repository regionSegmentationT15Repository;
    private final ProfitThresholdT7Repository profitThresholdT7Repository;
    private final SegmentationResultT15Repository segmentationResultT15Repository;
    private final RegionFlightCollapsedService regionFlightCollapsedService;
    private final RegionSegmentationParametersService regionSegmentationParametersService;
    private final RegionSegmentationLogService regionSegmentationLogService;
    private final LoadParameterService loadParameterService;
    private final StationHandbookService stationHandbookService;

    @Override
    public void saveAllSegments(String logId) {
        prepareNextSave();

        RegionSegmentationParameters parameters = regionSegmentationParametersService.getParameters();
        Integer maxSegments = parameters.getMaxSegments();

        List<RegionFlightCollapsed> flights = regionFlightCollapsedService.getAllRegionFlightCollapsed();

        Map<Integer, RegionFlightCollapsed> loadedFlights = getFlightsByType(flights, "Груженый");
        Map<Integer, RegionFlightCollapsed> emptyFlights = getFlightsByType(flights, "Порожний");

        Map<String, List<RegionFlightCollapsed>> emptyFlightsGroupedBySourceRegion = groupBySourceRegion(emptyFlights.values());
        Map<String, List<RegionFlightCollapsed>> loadedFlightsGroupedBySourceRegion = groupBySourceRegion(loadedFlights.values());

        List<List<RegionFlightCollapsed>> segmentations = getFirstSegmentation(loadedFlights, emptyFlightsGroupedBySourceRegion, logId);

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
        loadMarketAnalysisFeatures(segmentationResult);
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

    private Map<Integer, RegionFlightCollapsed> getFlightsByType(List<RegionFlightCollapsed> flights, String type) {
        return flights
                .stream()
                .filter(f -> f.getType().equals(type))
                .collect(Collectors.toMap(RegionFlightCollapsed::getId, Function.identity()));
    }

    private Map<String, List<RegionFlightCollapsed>> groupBySourceRegion(Collection<RegionFlightCollapsed> flights){
        return flights
                .stream()
                .filter(f -> !f.getSourceRegion().equals(f.getDestRegion()))
                .collect(Collectors.groupingBy(rf -> rf.getSourceRegion() + rf.getVolume(), Collectors.toList()));
    }

    private List<List<RegionFlightCollapsed>> getFirstSegmentation(
            Map<Integer, RegionFlightCollapsed> loadedFlights,
            Map<String, List<RegionFlightCollapsed>> emptyFlightsGroupedBySourceRegion, String logId) {
        List<List<RegionFlightCollapsed>> currentSegmentation = new ArrayList<>();
        for (RegionFlightCollapsed loaded : loadedFlights.values()) {
            List<RegionFlightCollapsed> empties = emptyFlightsGroupedBySourceRegion.get(loaded.getDestRegion() + loaded.getVolume());
            if (empties != null) {
                for (RegionFlightCollapsed empty : empties) {
                    List<RegionFlightCollapsed> segment = new ArrayList<>(Arrays.asList(loaded, empty));
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
            List<List<RegionFlightCollapsed>> segmentations,
            Map<String, List<RegionFlightCollapsed>> flightsGroupedBySourceRegion, String logId) {

        List<List<RegionFlightCollapsed>> newSegmentations = new ArrayList<>();
        for (List<RegionFlightCollapsed> segmentation : segmentations) {
            RegionFlightCollapsed lastSegmentPart = segmentation.get(segmentation.size() - 1);

            if (segmentation.size() % 2 == 0 && lastSegmentPart.getIsNextSegmentNofFound() != null && lastSegmentPart.getIsNextSegmentNofFound()) {
                continue;
            }

            List<RegionFlightCollapsed> nextSegmentPartFlights = flightsGroupedBySourceRegion.get(lastSegmentPart.getDestRegion() + lastSegmentPart.getVolume());
            if (nextSegmentPartFlights != null) {
                List<RegionFlightCollapsed> segmentationCopy = new ArrayList<>(segmentation);

                RegionFlightCollapsed firstFlight = nextSegmentPartFlights.get(0);
                segmentation.add(firstFlight);

                for (int i = 1; i < nextSegmentPartFlights.size(); i++) {
                    RegionFlightCollapsed nextSegmentPart = nextSegmentPartFlights.get(i);
                    List<RegionFlightCollapsed> newSegmentation = new ArrayList<>(segmentationCopy);
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

    public List<RegionSegmentationT15> mapSegmentsListToSegmentation(List<List<RegionFlightCollapsed>> segmentList) {
        List<RegionSegmentationT15> segmentations = new ArrayList<>();
        for (List<RegionFlightCollapsed> segmentationAnalysis : segmentList) {
            RegionSegmentationT15 finalSegmentation = new RegionSegmentationT15();
            List<RegionSegment> segments = new ArrayList<>();
            for (int i = 0; i < segmentationAnalysis.size(); i += 2) {
                RegionFlightCollapsed loaded = segmentationAnalysis.get(i);
                RegionFlightCollapsed empty = segmentationAnalysis.get(i + 1);

                List<RegionFlightCollapsed> segmentation = Arrays.asList(loaded, empty);
                BigDecimal profit = calculateProfit(segmentation);
                Integer travelDays = loaded.getTravelDays() + empty.getTravelDays();
                Integer loadDays = loaded.getSourceRegionLoadIdleDays();
                Integer unloadDays = loaded.getDestRegionUnloadIdleDays();

                RegionSegment segment = new RegionSegment(loaded.getSourceRegion(), loaded.getDestRegion(),
                        empty.getSourceRegion(), empty.getDestRegion(), profit, travelDays, loadDays, unloadDays,
                        loaded.getRateTariff(), empty.getRateTariff(), loaded.getFlightsAmount(), empty.getFlightsAmount());
                segments.add(segment);
            }

            finalSegmentation.setVolume(segmentationAnalysis.get(0).getVolume());
            finalSegmentation.setSegments(segments);
            setSegmentType(finalSegmentation);

            segmentations.add(finalSegmentation);
        }
        return segmentations;
    }

    private BigDecimal calculateProfit(List<RegionFlightCollapsed> segmentation) {
        RegionFlightCollapsed loaded = segmentation.get(0);
        RegionFlightCollapsed empty = segmentation.get(1);

        BigDecimal loadedAverageRate = loaded.getRateTariff().divide(new BigDecimal(loaded.getFlightsAmount()), 2, RoundingMode.HALF_UP);
        BigDecimal emptyAverageTariff = empty.getRateTariff().divide(new BigDecimal(empty.getFlightsAmount()), 2, RoundingMode.HALF_UP);
        int totalSegmentDays = loaded.getSourceRegionLoadIdleDays() + loaded.getDestRegionUnloadIdleDays() + loaded.getTravelDays() + empty.getTravelDays();

        return (loadedAverageRate.subtract(emptyAverageTariff))
                .divide(new BigDecimal(totalSegmentDays), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSegmentationTotalProfit(List<RegionSegment> segments) {
        BigDecimal totalAverageRate = segments.stream()
                .map(rs -> rs.getRate().divide(BigDecimal.valueOf(rs.getLoadedFlightsAmount()), 2, RoundingMode.HALF_UP))
                .reduce(new BigDecimal("0.00"), BigDecimal::add);

        BigDecimal totalAverageTariff = segments.stream()
                .map(rs -> rs.getTariff().divide(BigDecimal.valueOf(rs.getEmptyFlightsAmount()), 2, RoundingMode.HALF_UP))
                .reduce(new BigDecimal("0.00"), BigDecimal::add);

        int totalSegmentationDays = segments.stream()
                .mapToInt(rs -> rs.getLoadIdleDays() + rs.getUnloadIdleDays() + rs.getSegmentTravelDays())
                .sum();

        return (totalAverageRate.subtract(totalAverageTariff))
                .divide(new BigDecimal(totalSegmentationDays), 2, RoundingMode.HALF_UP);
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
            BigDecimal wholeProfit = calculateSegmentationTotalProfit(segments);
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

    private void loadMarketAnalysisFeatures(List<SegmentationResultT15> segmentationResultT15) {
        LoadParameters loadParameters = loadParameterService.getLoadParameters();

        File folder = new File(loadParameters.getSourceDataT14());
        File[] files = folder.listFiles();

        Integer daysToCheck = loadParameters.getDaysThresholdT14();
        File[] loadedFiles = findFiles(loadParameters.getLoadedMaskT14(), daysToCheck, files);
        File[] unloadedFiles = findFiles(loadParameters.getUnloadedMaskT14(), daysToCheck, files);

        if(loadedFiles.length == 0){
            return;
        }

        if(unloadedFiles.length == 0){
            return;
        }

        Map<String, Integer> loadedFlightInfo = groupedFlightInfos(loadedFiles);
        Map<String, Integer> unloadedFlightInfo = groupedFlightInfos(unloadedFiles);

        segmentationResultT15
                .forEach(s ->{
                    s.setFirstUtk(0);
                    s.setFirstMarket(0);
                    s.setFirstEmptyUtk(0);
                    s.setFirstEmptyMarket(0);

                    if(loadedFlightInfo.containsKey(s.getSourceRegion() + "true")){
                        s.setFirstUtk(loadedFlightInfo.get(s.getSourceRegion() + "true"));
                    }

                    if(loadedFlightInfo.containsKey(s.getSourceRegion() + "false")){
                        s.setFirstMarket(loadedFlightInfo.get(s.getSourceRegion() + "false"));
                    }

                    if(unloadedFlightInfo.containsKey(s.getSourceRegion() + "true")){
                        s.setFirstEmptyUtk(unloadedFlightInfo.get(s.getSourceRegion() + "true"));
                    }

                    if(unloadedFlightInfo.containsKey(s.getSourceRegion() + "false")){
                        s.setFirstEmptyMarket(unloadedFlightInfo.get(s.getSourceRegion() + "false"));
                    }
                });
    }

    private Map<String, Integer> groupedFlightInfos(File[] files){
        List<String> headers = ExcelUtils.extractHeaders(files[0].getAbsolutePath());
        Set<Integer> headerIndexes = new HashSet<>(formHeadersIndexes(headers).values());
        List<List<String>> rows = Arrays.stream(files)
                .flatMap(f -> ExcelUtils.read(f.getAbsolutePath(), headerIndexes).stream().skip(1))
                .collect(Collectors.toList());

        List<FlightInfo> flightInfos = rows.stream()
                .map(row -> new FlightInfo(row.get(0), row.get(1), row.get(2), row.get(3)))
                .peek(fi -> fi.setRegion(loadRegionByStationCode6(fi.getStationCode6())))
                .filter(fi -> fi.getRegion() != null)
                .collect(Collectors.toList());

        return groupFlightInfos(flightInfos);
    }

    @SneakyThrows
    private File[] findFiles(String filename, Integer days, File[] files) {
        LocalDate fromDate = LocalDate.now().minusDays(days);
        LocalDate now = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        List<String> filenames = new ArrayList<>();

        while (fromDate.isBefore(now)) {
            filenames.add(filename.trim() + " " + fromDate.format(DateTimeFormatter.ofPattern("yyyyMM")));
            fromDate = fromDate.plusMonths(1);
        }

        return Arrays.stream(files)
                .filter(File::isFile)
                .filter(file -> filenames
                        .stream()
                        .map(f -> f.endsWith(".xlsx") ? f : f + ".xlsx")
                        .anyMatch(name -> file.getAbsolutePath().endsWith(name)))
                .toArray(File[]::new);
    }

    private Map<String, Integer> formHeadersIndexes(List<String> headerRow) {
        Map<String, Integer> headers = new LinkedHashMap<>();
        EXCEL_FILE_HEADERS.forEach(header -> headers.put(header, 0));

        for (int i = 0; i < headerRow.size(); i++) {
            if (headers.containsKey(headerRow.get(i))) {
                headers.put(headerRow.get(i), i);
            }
        }
        return headers;
    }

    private Map<String, Integer> groupFlightInfos(List<FlightInfo> flightInfos) {
        return flightInfos
                .stream()
                .collect(Collectors.groupingBy(
                        fi -> fi.getRegion() + fi.getIsUtcFlight(),
                        Collectors.summingInt(FlightInfo::getCarsAmount)));
    }

    private String loadRegionByStationCode6(String code6){
        StationHandbook station = stationHandbookService.findStationByCode6(code6);
        if(station == null){
            return null;
        }
        String region = null;
        if(station.getRegion() != null){
            region = station.getRegion();
        }
        else if(station.getRoad() != null){
            region = station.getRoad();
        }
        return region;
    }
}
