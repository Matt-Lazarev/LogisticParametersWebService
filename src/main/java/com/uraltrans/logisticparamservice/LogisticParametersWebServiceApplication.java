package com.uraltrans.logisticparamservice;

import com.uraltrans.logisticparamservice.entity.postgres.FlightTimeDistance;
import com.uraltrans.logisticparamservice.repository.postgres.FlightTimeDistanceRepository;
import com.uraltrans.logisticparamservice.utils.excel.ExcelReaderWriterService;
import com.uraltrans.logisticparamservice.utils.mapper.SimpleExcelRowMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class LogisticParametersWebServiceApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(LogisticParametersWebServiceApplication.class, args);
//
//        FlightTimeDistanceRepository repository = context.getBean(FlightTimeDistanceRepository.class);
//
//        List<FlightTimeDistance> all = repository.findAll();
//
//        List<String> headers = Arrays.asList(
//                "ID", "Код станции отправления", "Код станции назначения", "Тип рейса", "Дистанция", "Время первозки");
//        ExcelReaderWriterService<FlightTimeDistance> writer = new ExcelReaderWriterService<>(new SimpleExcelRowMapper<>());
//
//        writer.write("Дистанция и время в пути.xlsx", "Дистанция и время в пути", headers, all, null);
    }
}
