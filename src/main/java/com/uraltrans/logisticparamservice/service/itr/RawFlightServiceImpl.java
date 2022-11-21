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
    private final RawFlightRepository rawFlightRepository;

    @Override
    public List<Map<String, Object>> getAllFlightsBetween(int days) {
        String from = LocalDate.now().minusDays(days).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String to = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return rawFlightRepository.getAllFlightsBetween(new String[]{from, to});
    }
}
