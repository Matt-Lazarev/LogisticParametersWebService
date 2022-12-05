package com.uraltrans.logisticparamservice;

import com.uraltrans.logisticparamservice.entity.postgres.FlightTimeDistance;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightTimeDistanceService;
import com.uraltrans.logisticparamservice.utils.excel.ExcelReaderWriterService;
import com.uraltrans.logisticparamservice.utils.excel.SimpleExcelRowMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;


@SpringBootApplication
public class LogisticParametersWebServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(LogisticParametersWebServiceApplication.class, args);

        ExcelReaderWriterService<Wagon> wagons = new ExcelReaderWriterService<>(new SimpleExcelRowMapper<>());
        List<Wagon> list = wagons.readAsList("API Запросы.xlsx", Wagon.class, null);

        FlightTimeDistanceService bean = run.getBean(FlightTimeDistanceService.class);
        List<FlightTimeDistance> allTimeDistances = bean.getAllTimeDistances();

        int counter = 1;
        int count = 0;
        for(Wagon wagon : list){
            for(FlightTimeDistance ftd : allTimeDistances){
                if(ftd.getDepartureStationCode().equalsIgnoreCase(wagon.getCode1())
                        && ftd.getDestinationStationCode().equals(wagon.getCode2()))
                {
                    System.out.println(counter + " " + ftd);
                    count++;
                }
            }
            counter++;
        }
        System.out.println(count);
    }
}


