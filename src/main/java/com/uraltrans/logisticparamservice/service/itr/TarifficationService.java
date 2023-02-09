package com.uraltrans.logisticparamservice.service.itr;

import com.uraltrans.logisticparamservice.repository.itr.TarifficationRepository;
import com.uraltrans.logisticparamservice.utils.CsvUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TarifficationService {
    private static final String TARIFFICATION_DIRECTORY = "tariffication_data";
    private static final List<String> TARIFFICATION_FILENAMES = Arrays.asList(
            "tariffications.txt", "distances.txt", "source-stations.txt", "dest-stations.txt", "foreign-stations.txt", "excluded-stations.txt", "included-stations.txt");

    private final TarifficationRepository tarifficationRepository;

    public void writeAllTarifficationFiles(){
        String tarifficationIds = getTarifficationIdsJoined();

        CsvUtils.writeCsvFile(TARIFFICATION_DIRECTORY, TARIFFICATION_FILENAMES.get(0), getAllTariffications());
        CsvUtils.writeCsvFile(TARIFFICATION_DIRECTORY, TARIFFICATION_FILENAMES.get(1), getAllDistances(tarifficationIds));
        CsvUtils.writeCsvFile(TARIFFICATION_DIRECTORY, TARIFFICATION_FILENAMES.get(2), getAllSourceStations(tarifficationIds));
        CsvUtils.writeCsvFile(TARIFFICATION_DIRECTORY, TARIFFICATION_FILENAMES.get(3), getAllDestStations(tarifficationIds));
        CsvUtils.writeCsvFile(TARIFFICATION_DIRECTORY, TARIFFICATION_FILENAMES.get(4), getAllForeignStations());
        CsvUtils.writeCsvFile(TARIFFICATION_DIRECTORY, TARIFFICATION_FILENAMES.get(5), getAllExcludedStations(tarifficationIds));
        CsvUtils.writeCsvFile(TARIFFICATION_DIRECTORY, TARIFFICATION_FILENAMES.get(6), getAllIncludedStations(tarifficationIds));
    }

    public List<Map<String, Object>> getAllTariffications() {
        return getAllTarifficationsInCurrentMonth();
    }

    public List<Map<String, Object>> getAllDistances(String tarifficationIds) {
        return tarifficationRepository.getAllDistances(tarifficationIds);
    }

    public List<Map<String, Object>> getAllSourceStations(String tarifficationIds) {
        return tarifficationRepository.getAllSourceStations(tarifficationIds);
    }

    public List<Map<String, Object>> getAllDestStations(String tarifficationIds) {
        return tarifficationRepository.getAllDestStations(tarifficationIds);
    }

    public List<Map<String, Object>> getAllForeignStations() {
        List<Integer> tarifficationIds = getTarifficationIds();
        List<Map<String, Object>> data = new ArrayList<>();
        for(Integer id : tarifficationIds){
            data.addAll(tarifficationRepository.getAllForeignStations(id));
        }
        return data;
    }

    public List<Map<String, Object>> getAllExcludedStations(String tarifficationIds) {
        return tarifficationRepository.getAllExcludedStations(tarifficationIds);
    }

    public List<Map<String, Object>> getAllIncludedStations(String tarifficationIds) {
        return tarifficationRepository.getAllIncludedStations(tarifficationIds);
    }

    private String getTarifficationIdsJoined(){
        return getAllTarifficationsInCurrentMonth()
                .stream()
                .map(map -> map.get("ID"))
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    private List<Integer> getTarifficationIds(){
        return getAllTarifficationsInCurrentMonth()
                .stream()
                .map(map -> map.get("ID"))
                .map(id -> (Integer) id)
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getAllTarifficationsInCurrentMonth() {
        LocalDate dateFrom = YearMonth.now().atDay(1);
        LocalDate dateTo   = YearMonth.now().atEndOfMonth();
        return tarifficationRepository.getAllTariffications(dateFrom.toString(), dateTo.toString());
    }
}
