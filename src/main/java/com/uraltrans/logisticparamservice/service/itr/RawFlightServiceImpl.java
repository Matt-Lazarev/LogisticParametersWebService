package com.uraltrans.logisticparamservice.service.itr;

import com.uraltrans.logisticparamservice.repository.itr.RawFlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RawFlightServiceImpl implements RawFlightService {
    private static final int PARTS_TO_DIVIDE_LONG_PERIOD = 3;
    private static final int MAX_DAYS_PERIOD = 20;

    private final RawFlightRepository rawFlightRepository;

    @Override
    public List<Map<String, Object>> getAllFlightsBetween(int days) {
        String[][] fromToDatePairs = divideDate(days);
        return rawFlightRepository.getAllFlightsBetween(fromToDatePairs);
    }

    private String[][] divideDate(int days){
        int parts = PARTS_TO_DIVIDE_LONG_PERIOD;
        if(days <= MAX_DAYS_PERIOD){
            return new String[][]{{
                LocalDate.now().minusDays(days).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }};
        }
        LocalDate[] dates = new LocalDate[parts*2];
        LocalDate current = LocalDate.now().minusDays(days);
        int i=0;
        while(i<dates.length-1){
            dates[i++] = current;
            current = current.plusDays((days / parts) - 1);
            dates[i++] = current;
            current = current.plusDays(1);
        }
        dates[dates.length-1] = LocalDate.now();

        String[][] fromToDatePairs = new String[parts][2];

        for(int j=0, k=0; j<dates.length; j+=2, k++){
            fromToDatePairs[k][0] = dates[j].format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            fromToDatePairs[k][1] = dates[j+1].format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        return fromToDatePairs;
    }
}
