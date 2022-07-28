package com.uraltrans.logisticparamservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uraltrans.logisticparamservice.dto.idle.FlightIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.FlightIdle;
import com.uraltrans.logisticparamservice.repository.postgres.FlightIdleRepository;
import com.uraltrans.logisticparamservice.utils.excel.ExcelReaderWriterService;
import com.uraltrans.logisticparamservice.utils.excel.SimpleExcelRowMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class LogisticParametersWebServiceApplication {

    public static void main(String[] args) throws IOException, URISyntaxException {
        ApplicationContext context = SpringApplication.run(LogisticParametersWebServiceApplication.class, args);

//        List<FlightIdle> all = context.getBean(FlightIdleRepository.class).findAll();
//
//        List<String> headers = Arrays.asList("ID",
//                "Станция", "Код станции",
//                "Груз", "Код груза", "Тип рейса", "Объем", "Простой под погрузкой", "Простой под выгрузкой");
//
////        FlightProfitRepository repository = context.getBean(FlightProfitRepository.class);
////        List<FlightProfit> all = repository.findAll();
////        List<String> headers = Arrays.asList(
////                "ID", "Дата отправления", "Объем кузова", "Код станции отправления", "Код станции назначения",
////                "Код груза", "Груз", "Доход за рейсы", "Доход в валюте", "Среднее время перевозки", "Количество  рейсов ", "Код валюты");
////
//        ExcelReaderWriterService<FlightIdle> writer = new ExcelReaderWriterService<>(new SimpleExcelRowMapper<>());
//        writer.write("Простои.xlsx", "Простои", headers, all, null);
    }
}
