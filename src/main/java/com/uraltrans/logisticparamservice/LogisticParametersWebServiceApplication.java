package com.uraltrans.logisticparamservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uraltrans.logisticparamservice.dto.idle.FlightIdleDto;
import com.uraltrans.logisticparamservice.dto.idle.LoadUnloadIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.FlightIdle;
import com.uraltrans.logisticparamservice.entity.postgres.FlightRequirement;
import com.uraltrans.logisticparamservice.repository.postgres.FlightIdleRepository;
import com.uraltrans.logisticparamservice.repository.postgres.FlightRepository;
import com.uraltrans.logisticparamservice.repository.postgres.FlightRequirementRepository;
import com.uraltrans.logisticparamservice.utils.excel.ExcelReaderWriterService;
import com.uraltrans.logisticparamservice.utils.excel.SimpleExcelRowMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class LogisticParametersWebServiceApplication {

    public static void main(String[] args) throws IOException, URISyntaxException {
        ApplicationContext context = SpringApplication.run(LogisticParametersWebServiceApplication.class, args);


//        List<LoadUnloadIdleDto> all = context.getBean(FlightRepository.class).groupCarLoadUnloadIdle()
//                .stream()
//                .filter(f -> f.getCarLoadIdleDays() != null || f.getCarUnloadIdleDays() != null)
//                .collect(Collectors.toList());
//        List<String> headers = Arrays.asList("Ст. отправления", "Код ст. отправления",
//                "Ст. назначения", "Код ст. назначения", "Объем", "Груз", "Код груза", "Простой под подгрузкой", "Простой под выгрузкой");
//
//        ExcelReaderWriterService<LoadUnloadIdleDto> writer = new ExcelReaderWriterService<>(new SimpleExcelRowMapper<>());
//        writer.write("Простои.xlsx", "Простои", headers, all, null);


//
//        RestTemplate rt = new RestTemplate();
//        FlightIdle[] all = rt.getForObject("http://10.168.1.5:8080/result/load_days", FlightIdle[].class);
//
//
////        List<FlightRequirement> all = context.getBean(FlightRequirementRepository.class).findAll();
////
//        List<String> headers = Arrays.asList("ID", "Ст. отправления", "Ст. назначения", "Груз", "Код груза", "Объем");
//
//        ExcelReaderWriterService<FlightIdle> writer = new ExcelReaderWriterService<>(new SimpleExcelRowMapper<>());
//        writer.write("Простои.xlsx", "Простои", headers, Arrays.stream(all).collect(Collectors.toList()), null);
    }
}
