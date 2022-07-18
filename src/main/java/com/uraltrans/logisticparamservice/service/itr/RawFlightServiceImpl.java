package com.uraltrans.logisticparamservice.service.itr;

import com.uraltrans.logisticparamservice.repository.itr.RawFlightRepository;
import com.uraltrans.logisticparamservice.service.itr.RawFlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class RawFlightServiceImpl implements RawFlightService {

    private final RawFlightRepository rawFlightRepository;

    @Override
    public List<Map<String, Object>> getAllFlightsBetween(LocalDate from, LocalDate to) {
        String fromDate = from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String toDate = to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return rawFlightRepository.getAllFlightsBetween(fromDate, toDate);
    }
}
