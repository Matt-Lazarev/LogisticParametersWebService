package com.uraltrans.logisticparamservice.service.itr;

import com.uraltrans.logisticparamservice.repository.itr.TarifficationRepositoryImpl;
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
public class TarifficationServiceImpl implements TarifficationService {
    private static final String TARIFFICATION_DIRECTORY = "tariffication_data";
    private static final List<String> TARIFFICATION_FILENAMES = Arrays.asList(
            "тарификация.txt", "таблица_расстояний.txt", "станции_отправления.txt", "станции_назначения.txt", "заграничные_станции.txt", "исключая_станции.txt", "включая_станции.txt");

    private final TarifficationRepositoryImpl tarifficationRepository;

    @Override
    public void writeAllTarifficationFiles(){
        List<Map<String, Object>> tariffications = getAllTarifficationsInCurrentMonth();
        loadAdditionalColumns(tariffications);

        List<Integer> tarifficationIds = getTarifficationIds(tariffications);
        String tarifficationIdsJoined = getTarifficationIdsJoined(tarifficationIds);

        CsvUtils.writeCsvFile(TARIFFICATION_DIRECTORY, TARIFFICATION_FILENAMES.get(0), tariffications);
        CsvUtils.writeCsvFile(TARIFFICATION_DIRECTORY, TARIFFICATION_FILENAMES.get(1), getAllDistances(tarifficationIdsJoined));
        CsvUtils.writeCsvFile(TARIFFICATION_DIRECTORY, TARIFFICATION_FILENAMES.get(2), getAllSourceStations(tarifficationIdsJoined));
        CsvUtils.writeCsvFile(TARIFFICATION_DIRECTORY, TARIFFICATION_FILENAMES.get(3), getAllDestStations(tarifficationIdsJoined));
        CsvUtils.writeCsvFile(TARIFFICATION_DIRECTORY, TARIFFICATION_FILENAMES.get(4), getAllForeignStations(tarifficationIds));
        CsvUtils.writeCsvFile(TARIFFICATION_DIRECTORY, TARIFFICATION_FILENAMES.get(5), getAllExcludedStations(tarifficationIdsJoined));
        CsvUtils.writeCsvFile(TARIFFICATION_DIRECTORY, TARIFFICATION_FILENAMES.get(6), getAllIncludedStations(tarifficationIdsJoined));
    }

    private List<Map<String, Object>> getAllTarifficationsInCurrentMonth() {
        LocalDate dateFrom = YearMonth.now().atDay(1);
        LocalDate dateTo   = YearMonth.now().atEndOfMonth();
        return tarifficationRepository.getAllTariffications(dateFrom.toString(), dateTo.toString());
    }

    private void loadAdditionalColumns(List<Map<String, Object>> tariffications){
        for(Map<String, Object> row : tariffications){
            Integer id = (Integer) row.get("ID");
            List<Map<String, Object>> additionalColumns = tarifficationRepository.getAllAdditionalColumns(id);
            if(additionalColumns.size() == 0){
                row.put("KL_PRED_TRANSKIND_ID_TRANSKIND", null);
            }
            else {
                Map<String, Object> firstAdditionalRow = additionalColumns.get(0);
                row.put("KL_PRED_TRANSKIND_ID_TRANSKIND", firstAdditionalRow.get("KL_PRED_TRANSKIND_ID_TRANSKIND"));
            }
        }
    }

    private List<Map<String, Object>> getAllDistances(String tarifficationIds) {
        return tarifficationRepository.getAllDistances(tarifficationIds);
    }

    private List<Map<String, Object>> getAllSourceStations(String tarifficationIds) {
        return tarifficationRepository.getAllSourceStations(tarifficationIds);
    }

    private List<Map<String, Object>> getAllDestStations(String tarifficationIds) {
        return tarifficationRepository.getAllDestStations(tarifficationIds);
    }

    private List<Map<String, Object>> getAllForeignStations(List<Integer> tarifficationIds) {
        List<Map<String, Object>> data = new ArrayList<>();
        for(Integer id : tarifficationIds){
            data.addAll(tarifficationRepository.getAllForeignStations(id));
        }
        return data;
    }

    private List<Map<String, Object>> getAllExcludedStations(String tarifficationIds) {
        return tarifficationRepository.getAllExcludedStations(tarifficationIds);
    }

    private List<Map<String, Object>> getAllIncludedStations(String tarifficationIds) {
        return tarifficationRepository.getAllIncludedStations(tarifficationIds);
    }

    private List<Integer> getTarifficationIds(List<Map<String, Object>> tariffications){
        return tariffications
                .stream()
                .map(map -> map.get("ID"))
                .map(id -> (Integer) id)
                .collect(Collectors.toList());
    }

    private String getTarifficationIdsJoined(List<Integer> ids){
        return ids
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }
}
