package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.dto.idle.LoadIdleDto;
import com.uraltrans.logisticparamservice.dto.idle.UnloadIdleDto;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FlightRepositoryTest {

    @Autowired
    FlightRepository flightRepository;

    @Test
    @DisplayName("Тест среднего времени погрузки в днях после группировки")
    public void testGroupCarLoadIdle() {
        List<LoadIdleDto> loadIdlesGroupedDto = flightRepository.groupCarLoadIdle();
        int actual = loadIdlesGroupedDto
                .stream()
                .filter(carLoadIdle -> carLoadIdle.getSourceStation().equals("Чепецкая"))
                .filter(carLoadIdle -> carLoadIdle.getVolume().doubleValue() == 88.0)
               // .filter(carLoadIdle -> carLoadIdle.getCargo().equals("УДОБРЕНИЯ ХИМИЧЕСКИЕ И МИНЕРАЛЬНЫЕ ВСЯКИЕ, НЕ ПОИМЕНОВАННЫЕ В АЛФАВИТЕ"))
                .mapToInt(LoadIdleDto::getCarLoadIdleDays)
                .findFirst()
                .orElse(0);

        List<Flight> allFlights = flightRepository.findAll();
        int expected = (int) Math.ceil(allFlights
                .stream()
                .filter(flight -> flight.getSourceStation().equals("Чепецкая"))
                .filter(flight -> flight.getVolume().doubleValue() == 88.0)
                .filter(flight -> flight.getCargo().equals("УДОБРЕНИЯ ХИМИЧЕСКИЕ И МИНЕРАЛЬНЫЕ ВСЯКИЕ, НЕ ПОИМЕНОВАННЫЕ В АЛФАВИТЕ"))
                .filter(flight -> flight.getCarLoadIdleDays() != null)
                .mapToInt(Flight::getCarLoadIdleDays)
                .average()
                .orElse(0.0));

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Тест среднего времени выгрузки в днях после группировки")
    public void testGroupCarUnloadIdle() {
        List<UnloadIdleDto> carLoadIdlesGrouped = flightRepository.groupCarUnloadIdle();
        int actual = carLoadIdlesGrouped
                .stream()
                .filter(carLoadIdle -> carLoadIdle.getDestStation().equals("Галаба (эксп.)"))
                .filter(carLoadIdle -> carLoadIdle.getVolume().doubleValue() == 122.0)
        //        .filter(carLoadIdle -> carLoadIdle.getCargo().equals("ЧЕЧЕВИЦА"))
                .mapToInt(UnloadIdleDto::getCarUnloadIdleDays)
                .findFirst()
                .orElse(0);

        List<Flight> allFlights = flightRepository.findAll();
        int expected = (int) Math.ceil(allFlights
                .stream()
                .filter(flight -> flight.getDestStation().equals("Галаба (эксп.)"))
                .filter(flight -> flight.getVolume().doubleValue() == 122.0)
                .filter(flight -> flight.getCargo().equals("ЧЕЧЕВИЦА"))
                .filter(flight -> flight.getCarUnloadIdleDays() != null)
                .mapToInt(Flight::getCarUnloadIdleDays)
                .average()
                .orElse(0.0));

        assertEquals(expected, actual);
    }
}