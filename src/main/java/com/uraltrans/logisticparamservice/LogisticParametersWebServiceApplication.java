package com.uraltrans.logisticparamservice;

import com.uraltrans.logisticparamservice.entity.postgres.FlightProfit;
import com.uraltrans.logisticparamservice.repository.postgres.FlightProfitRepository;
import com.uraltrans.logisticparamservice.utils.excel.ExcelReaderWriterService;
import com.uraltrans.logisticparamservice.utils.excel.SimpleExcelRowMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class LogisticParametersWebServiceApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(LogisticParametersWebServiceApplication.class, args);

//        FlightProfitRepository repository = context.getBean(FlightProfitRepository.class);
//        List<FlightProfit> all = repository.findAll();
//        List<String> headers = Arrays.asList(
//                "ID", "Дата отправления", "Объем кузова", "Код станции отправления", "Код станции назначения",
//                "Код груза", "Груз", "Доход за рейсы", "Доход в валюте", "Среднее время перевозки", "Количество  рейсов ", "Код валюты");
//
//        ExcelReaderWriterService<FlightProfit> writer = new ExcelReaderWriterService<>(new SimpleExcelRowMapper<>());
//        writer.write("Доходность.xlsx", "Доходность", headers, all, null);
    }
}
