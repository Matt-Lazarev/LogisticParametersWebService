package com.uraltrans.logisticparamservice;

import com.uraltrans.logisticparamservice.utils.mapper.SimpleExcelRowMapper;
import com.uraltrans.logisticparamservice.utils.model.Flight;
import com.uraltrans.logisticparamservice.repository.postgres.FlightRepository;
import com.uraltrans.logisticparamservice.utils.excel.ExcelReaderWriterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UnloadIdleTest {
    @Autowired
    FlightRepository repository;

    @Test
    void compareUnloadIdleInDays(){
        ExcelReaderWriterService<Flight> service = new ExcelReaderWriterService<>(new SimpleExcelRowMapper<>());

        List<Flight> apiData = repository.findAll();
        apiData = apiData.stream()
                .filter(flight -> flight.getLoaded().equalsIgnoreCase("груж"))
                .collect(Collectors.toList());

        List<Flight> excelData = service.readAsList("unload_idle.xlsx", Flight.class, null);
        excelData = excelData
                .stream()
                .filter(el -> el.getCarNumber()!=null && el.getInvNumber()!=null && el.getIdle()!=null)
                .collect(Collectors.toList());

        int threshold = 30;
        int count = 0;
        for(Flight excelFlight : excelData){
            for(Flight apiFlight : apiData){
                if(Objects.equals(excelFlight.getCarNumber(), apiFlight.getCarNumber())
                        && Objects.equals(excelFlight.getInvNumber(), apiFlight.getInvNumber())){

                    Integer apiIdleUnload = apiFlight.getCarUnloadIdleDays();
                    if(apiIdleUnload != null && excelFlight.getIdle().intValue() == apiIdleUnload
                            || excelFlight.getIdle().intValue()==0 || excelFlight.getIdle().intValue() > threshold){
                        count++;
                    }
                    else{
                        System.out.println(excelFlight + "----------" + apiFlight);
                    }
                }
            }
        }
        assertEquals(excelData.size(), count);
    }
}
