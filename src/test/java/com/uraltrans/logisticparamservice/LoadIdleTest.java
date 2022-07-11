package com.uraltrans.logisticparamservice;

import com.uraltrans.logisticparamservice.utils.mapper.SimpleExcelRowMapper;
import com.uraltrans.logisticparamservice.repository.postgres.FlightRepository;
import com.uraltrans.logisticparamservice.utils.excel.ExcelReaderWriterService;
import com.uraltrans.logisticparamservice.utils.model.Flight;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LoadIdleTest {

    @Autowired
    FlightRepository repository;

    @Test
    void compareLoadIdleInDays() {
        ExcelReaderWriterService<Flight> service = new ExcelReaderWriterService<>(new SimpleExcelRowMapper<>());

        List<com.uraltrans.logisticparamservice.entity.postgres.Flight> apiData = repository.findAll();
        apiData = apiData.stream()
                .filter(flight -> flight.getLoaded().equalsIgnoreCase("груж"))
                .collect(Collectors.toList());

        List<Flight> excelData = service.readAsList("load_idle.xlsx", Flight.class, null);
        long excelCount = excelData
                .stream()
                .filter(el -> el.getCarNumber() != null && el.getInvNumber() != null && el.getIdle() != null)
                .count();

        int threshold = 30;
        int count = 0;
        for (Flight excelFlight : excelData) {
            for (com.uraltrans.logisticparamservice.entity.postgres.Flight apiFlight : apiData) {
                if (Objects.equals(excelFlight.getCarNumber(), apiFlight.getCarNumber())
                        && Objects.equals(excelFlight.getInvNumber(), apiFlight.getInvNumber())) {


                    Integer apiIdleLoad = apiFlight.getCarLoadIdleDays();
                    if (apiIdleLoad != null && excelFlight.getIdle().intValue() == apiIdleLoad
                            || excelFlight.getIdle().intValue() == 0 || excelFlight.getIdle().intValue() > threshold) {
                        count++;
                    } else {
                        System.out.println(excelFlight + "----------" + apiFlight);
                    }
                }
            }
        }
        assertEquals(excelCount, count);
    }


    @Test
    public void countDualAndAbnormalFlights() {
        int threshold = 30;
        List<com.uraltrans.logisticparamservice.entity.postgres.Flight> apiData = repository.findAll();

        long c1 = apiData
                .stream()
                .filter(flight -> flight.getLoaded().equalsIgnoreCase("груж"))
                .filter(flight -> flight.getComment() != null)
                .count();

        long c2 = apiData
                .stream()
                .filter(flight -> flight.getLoaded().equalsIgnoreCase("груж"))
                .filter(flight -> flight.getCarUnloadIdleDays() != null)
                .filter(flight -> flight.getCarUnloadIdleDays() < 0 || flight.getCarUnloadIdleDays() > threshold)
                .count();
        System.out.println(c1 + c2);
    }

    @Test
    public void test() {
        ExcelReaderWriterService<Flight> service = new ExcelReaderWriterService<>(new SimpleExcelRowMapper<>());

        List<com.uraltrans.logisticparamservice.entity.postgres.Flight> apiData = repository.findAll();
        List<Flight> excelData = service.readAsList("load_idle.xlsx", Flight.class, null);
        excelData = excelData
                .stream()
                .filter(el -> el.getArriveDate() != null)
                .filter(el -> el.getArriveDate().isAfter(LocalDate.of(2022, 4, 30)))
                .collect(Collectors.toList());

        int count = 0;
        for (Flight excelFlight : excelData) {
            for (com.uraltrans.logisticparamservice.entity.postgres.Flight apiFlight : apiData) {
                if (Objects.equals(excelFlight.getCarNumber(), apiFlight.getCarNumber())
                        && Objects.equals(excelFlight.getInvNumber(), apiFlight.getInvNumber())) {

                    Integer apiIdleLoad = apiFlight.getCarLoadIdleDays();
                    if (apiIdleLoad != null && excelFlight.getIdle().intValue() == apiIdleLoad) {
                        count++;
                    } else {
                        System.out.println(excelFlight + "----------" + apiFlight);
                    }
                }
            }
        }
        assertEquals(excelData.size(), count);
    }

}
