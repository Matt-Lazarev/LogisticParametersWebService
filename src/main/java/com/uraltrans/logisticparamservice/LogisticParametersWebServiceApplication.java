package com.uraltrans.logisticparamservice;

import com.uraltrans.logisticparamservice.entity.postgres.NoDetailsWagon;
import com.uraltrans.logisticparamservice.repository.postgres.NoDetailsWagonRepository;
import com.uraltrans.logisticparamservice.utils.excel.ExcelReaderWriterService;
import com.uraltrans.logisticparamservice.utils.excel.SimpleExcelRowMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.List;


@SpringBootApplication
public class LogisticParametersWebServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(LogisticParametersWebServiceApplication.class, args);

        ExcelReaderWriterService<NoDetailsWagon> excelReaderWriterService = new ExcelReaderWriterService<>(new SimpleExcelRowMapper<>());

        List<String> headers = Arrays.asList("id", "departureDate", "departureStation", "destinationStation", "departureStationName",
                "destinationStationName", "departureRoadName", "destinationRoadName", "cargoId", "wagonType", "volume", "carNumber",
                "p02", "p06", "p20", "statusWagon", "distanceFromCurrentStation", "daysBeforeDatePlanRepair", "restRun", "refurbished",
                "idleDislocationStation");
        NoDetailsWagonRepository bean = run.getBean(NoDetailsWagonRepository.class);
        List<NoDetailsWagon> wagons = bean.findAll();

        excelReaderWriterService.write("Вагоны без реквизитов.xlsx", "Список", headers, wagons, null);
    }
}


