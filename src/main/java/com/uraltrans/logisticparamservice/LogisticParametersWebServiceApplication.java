package com.uraltrans.logisticparamservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uraltrans.logisticparamservice.dto.idle.FlightIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.FlightIdle;
import com.uraltrans.logisticparamservice.entity.postgres.FlightRequirement;
import com.uraltrans.logisticparamservice.repository.postgres.FlightIdleRepository;
import com.uraltrans.logisticparamservice.repository.postgres.FlightRequirementRepository;
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

//        List<FlightRequirement> all = context.getBean(FlightRequirementRepository.class).findAll();
//
//        List<String> headers = Arrays.asList("ID",
//                "Объем ОТ", "Объем ДО",
//                "Станция отправления", "Станция назначения",
//                "Рейсов в плане", "Завершенные рейсы", "Рейсов в работе", "Потребность в рейсах");
//
//        ExcelReaderWriterService<FlightRequirement> writer = new ExcelReaderWriterService<>(new SimpleExcelRowMapper<>());
//        writer.write("Потребность в рейсах2.xlsx", "Потребность", headers, all, null);
    }
}
