package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.excelt14.FlightInfo;
import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.entity.postgres.SegmentationAnalysisT14;
import com.uraltrans.logisticparamservice.entity.rjd.SegmentationAnalysisT13;
import com.uraltrans.logisticparamservice.repository.postgres.SegmentationAnalysisT14Repository;
import com.uraltrans.logisticparamservice.repository.rjd.SegmentationAnalysisT13Repository;
import com.uraltrans.logisticparamservice.service.mapper.mapstruct.SegmentationAnalysisT14Mapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.SegmentationAnalysisT14Service;
import com.uraltrans.logisticparamservice.utils.ExcelUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SegmentationAnalysisT14ServiceImpl implements SegmentationAnalysisT14Service {
    private static final List<String> EXCEL_FILE_HEADERS = Arrays.asList(
            "Месяц год отправления", "Станция отправления Код6", "Кол-во вагоноотправок", "Вагоноотправки УТК");
    private final SegmentationAnalysisT13Repository segmentationAnalysisT13Repository;
    private final SegmentationAnalysisT14Repository segmentationAnalysisT14Repository;
    private final SegmentationAnalysisT14Mapper segmentationAnalysisT14Mapper;
    private final LoadParameterService loadParameterService;


    @Override
    public void saveAllSegmentsT14() {
        prepareNextSave();

        List<SegmentationAnalysisT13> segmentsT13 = segmentationAnalysisT13Repository.findAll();
        List<SegmentationAnalysisT14> segmentsT14 = segmentationAnalysisT14Mapper.mapToSegmentsT14List(segmentsT13);
        loadFeatures(segmentsT14);
        segmentationAnalysisT14Repository.saveAll(segmentsT14);
    }

    @Override
    public List<SegmentationAnalysisT14> getAllSegmentsT14() {
        return segmentationAnalysisT14Repository.findAll();
    }

    private void prepareNextSave() {
        segmentationAnalysisT14Repository.deleteAll();
    }

    private void loadFeatures(List<SegmentationAnalysisT14> segmentsT14) {
        LoadParameters loadParameters = loadParameterService.getLoadParameters();

        File folder = new File(loadParameters.getSourceDataT14());
        File[] files = folder.listFiles();

        Integer daysToCheck = loadParameters.getDaysThresholdT14();
        File[] loadedFiles = findFiles(loadParameters.getLoadedMaskT14(), daysToCheck, files);
        File[] unloadedFiles = findFiles(loadParameters.getUnloadedMaskT14(), daysToCheck, files);

        Map<String, Integer> loadedFlightInfo = groupedFlightInfos(loadedFiles);
        Map<String, Integer> unloadedFlightInfo = groupedFlightInfos(unloadedFiles);

        segmentsT14
                .forEach(s ->{
                    s.setFirstUtk(0);
                    s.setFirstMarket(0);
                    s.setFirstEmptyUtk(0);
                    s.setFirstEmptyMarket(0);

                    if(loadedFlightInfo.containsKey(s.getFromStation() + "true")){
                        s.setFirstUtk(loadedFlightInfo.get(s.getFromStation() + "true"));
                    }

                    if(loadedFlightInfo.containsKey(s.getFromStation() + "false")){
                        s.setFirstMarket(loadedFlightInfo.get(s.getFromStation() + "false"));
                    }

                    if(unloadedFlightInfo.containsKey(s.getFromStation() + "true")){
                        s.setFirstEmptyUtk(unloadedFlightInfo.get(s.getFromStation() + "true"));
                    }

                    if(unloadedFlightInfo.containsKey(s.getFromStation() + "false")){
                        s.setFirstEmptyMarket(unloadedFlightInfo.get(s.getFromStation() + "false"));
                    }
                });
    }

    private Map<String, Integer> groupedFlightInfos(File[] files){
        List<List<List<String>>> rows = Arrays.stream(files)
                .map(f -> ExcelUtils.read(f.getAbsolutePath()))
                .collect(Collectors.toList());

        List<String> headerRow = rows.get(0).get(0);
        Map<String, Integer> headers = formHeadersIndexes(headerRow);

        List<FlightInfo> flightInfos = rows.stream()
                .flatMap(lists -> lists.stream().skip(1))
                .map(row -> reduceRow(row, headers))
                .map(row -> new FlightInfo(row.get(0), row.get(1), row.get(2), row.get(3)))
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

    private List<String> reduceRow(List<String> row, Map<String, Integer> headers) {
        List<String> reducedRow = new ArrayList<>();
        headers.values().forEach(index -> reducedRow.add(row.get(index)));
        return reducedRow;
    }

    private Map<String, Integer> groupFlightInfos(List<FlightInfo> flightInfos) {
        return flightInfos
                .stream()
                .collect(Collectors.groupingBy(
                        fi -> fi.getStationCode5() + fi.getIsUtcFlight(),
                        Collectors.summingInt(FlightInfo::getCarsAmount)));
    }
}
