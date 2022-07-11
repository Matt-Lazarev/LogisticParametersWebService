package com.uraltrans.logisticparamservice.service.impl;

import com.uraltrans.logisticparamservice.dto.request.LoadDataRequestDto;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import com.uraltrans.logisticparamservice.mapper.FlightMapper;
import com.uraltrans.logisticparamservice.repository.postgres.FlightRepository;
import com.uraltrans.logisticparamservice.service.abstr.RawFlightService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest
class FlightIdleServiceImplTest {

    @Autowired
    FlightIdleServiceImpl underTest;

    @MockBean
    FlightRepository flightRepository;

    @MockBean
    FlightMapper flightMapper;

    @MockBean
    RawFlightService rawFlightService;

    final LoadDataRequestDto dto = new LoadDataRequestDto(30, 30, 30, 30, 30);

    @Test
    @DisplayName("Тест расчета времени погрузки для одного рейса с учетом сдвоек")
    public void canSaveAllFlights_testLoadIdleDays() {
        List<Flight> flights = getData_testLoadIdleDays();

        when(flightMapper.mapRawFlightsDataToFlightsList(anyList()))
                .thenReturn(flights);

        underTest.saveAllFlights(dto);

        assertAll(
                () -> assertNull(flights.get(0).getCarLoadIdleDays()),
                () -> assertEquals(
                        getDaysBetween(flights.get(1).getArriveToSourceStationDate(), flights.get(1).getSendDate()),
                        flights.get(1).getCarLoadIdleDays()),
                () -> assertEquals(
                        getDaysBetween(flights.get(2).getArriveToSourceStationDate(), flights.get(2).getSendDate()),
                        flights.get(2).getCarLoadIdleDays())
        );
    }

    @Test
    @DisplayName("Тест сдвойки")
    public void canSaveAllFlights_testLoadIdleDays_dualFlight() {
        List<Flight> flights = getData_testLoadIdleDays();

        when(flightMapper.mapRawFlightsDataToFlightsList(anyList()))
                .thenReturn(flights);

        underTest.saveAllFlights(dto);

        assertAll(
                () -> assertNull(flights.get(0).getCarLoadIdleDays()),
                () -> assertEquals("сдвойка", flights.get(0).getComment())
        );
    }

    @Test
    @DisplayName("Тест расчета времени выгрузки для одного рейса")
    public void canSaveAllFlights_testUnloadIdleDays() {
        List<Flight> flights = getData_testUnloadIdleDays();

        when(flightMapper.mapRawFlightsDataToFlightsList(anyList()))
                .thenReturn(flights);

        underTest.saveAllFlights(dto);

        assertAll(
                () -> assertNull(flights.get(0).getCarLoadIdleDays()),
                () -> assertEquals(
                        getDaysBetween(flights.get(1).getArriveToDestStationDate(), flights.get(1).getNextFlightStartDate()),
                        flights.get(1).getCarUnloadIdleDays()),
                () -> assertEquals(
                        getDaysBetween(flights.get(2).getArriveToDestStationDate(), flights.get(2).getNextFlightStartDate()),
                        flights.get(2).getCarUnloadIdleDays())
        );
    }

    @Test
    @DisplayName("Тест времени погрузки с учетом заграничных рейсов")
    public void canSaveAllFlights_testLoadIdleDays_borderCrossingFlight(){
        Timestamp borderCrossingTimestamp = Timestamp.valueOf(LocalDateTime.of(2022, 7, 1, 0, 0));

        List<Flight> flights = getData_testLoadIdleDays();
        flights.get(4).setArriveToDestStationDate(borderCrossingTimestamp);

        when(flightMapper.mapRawFlightsDataToFlightsList(anyList()))
                .thenReturn(flights);

        underTest.saveAllFlights(dto);

        assertAll(
                () -> assertEquals(
                        getDaysBetween(flights.get(3).getArriveToSourceStationDate(), flights.get(4).getSendDate()),
                        flights.get(3).getCarLoadIdleDays()),
                () -> assertNull(flights.get(4).getCarLoadIdleDays())
        );
    }

    @Test
    @DisplayName("Тест времени выгрузки с учетом заграничных рейсов")
    public void canSaveAllFlights_testUnloadIdleDays_borderCrossingFlight(){
        Timestamp borderCrossingTimestamp = Timestamp.valueOf(LocalDateTime.of(2022, 7, 1, 0, 0));

        List<Flight> flights = getData_testUnloadIdleDays();
        flights.get(4).setArriveToDestStationDate(borderCrossingTimestamp);

        when(flightMapper.mapRawFlightsDataToFlightsList(anyList()))
                .thenReturn(flights);

        underTest.saveAllFlights(dto);

        assertAll(
                () -> assertEquals(
                        getDaysBetween(flights.get(4).getArriveToDestStationDate(), flights.get(4).getNextFlightStartDate()),
                        flights.get(3).getCarUnloadIdleDays()),
                () -> assertNull(flights.get(4).getCarUnloadIdleDays())
        );
    }

    @Test
    public void canResolveDuplicatesTest(){
        List<Flight> data = getDuplicateFlightsData();

        List<Flight> result = underTest.filterDuplicateFlights(data);

        assertAll(
                () -> assertEquals(data.get(2).getArriveToDestStationDate(), data.get(0).getArriveToDestStationDate()),
                () -> assertEquals(data.get(2).getNextFlightStartDate(), data.get(0).getNextFlightStartDate()),
                () -> assertNotEquals(data.get(2).getSendDate(), data.get(0).getSendDate()),
                () -> assertEquals(result.size(), data.size()-2)
        );
    }

    private List<Flight> getData_testLoadIdleDays() {
        List<Flight> flights = getData();

        Timestamp timestamp1 = Timestamp.valueOf(LocalDateTime.of(2022, 6, 20, 0, 0));
        Timestamp timestamp2 = Timestamp.valueOf(LocalDateTime.of(2022, 7, 4, 0, 0));

        for (Flight flight : flights) {
            flight.setArriveToSourceStationDate(timestamp1);
            flight.setSendDate(timestamp2);
        }

        return flights;
    }

    private List<Flight> getData_testUnloadIdleDays() {
        List<Flight> flights = getData();

        Timestamp timestamp1 = Timestamp.valueOf(LocalDateTime.of(2022, 6, 20, 0, 0));
        Timestamp timestamp2 = Timestamp.valueOf(LocalDateTime.of(2022, 7, 14, 0, 0));

        for (Flight flight : flights) {
            flight.setArriveToDestStationDate(timestamp1);
            flight.setNextFlightStartDate(timestamp2);
        }

        return flights;
    }

    private List<Flight> getData(){
        List<Flight> flights = new ArrayList<>();

        flights.add(new Flight()
                .setAid(1)
                .setSourceStation("Москва")
                .setDestStation("Питер")
                .setLoaded("Груж")
                .setCarNumber(123)
                .setInvNumber("T123")
        );

        flights.add(new Flight()
                .setAid(2)
                .setSourceStation("Питер")
                .setDestStation("Воронеж")
                .setLoaded("Груж")
                .setCarNumber(123)
                .setInvNumber("T123")
        );

        flights.add(new Flight()
                .setAid(3)
                .setSourceStation("Питер")
                .setDestStation("Воронеж")
                .setLoaded("Груж")
                .setCarNumber(123)
                .setInvNumber("T123")
        );

        flights.add(new Flight()
                .setAid(4)
                .setSourceStation("Шарья")
                .setDestStation("Казахстан")
                .setLoaded("Груж")
                .setCarNumber(123)
                .setInvNumber("T123")
        );

        flights.add(new Flight()
                .setAid(5)
                .setSourceStation("Арыс")
                .setDestStation("Казахстан")
                .setLoaded("Груж")
                .setCarNumber(123)
                .setInvNumber(null)
        );

        return flights;
    }

    private List<Flight> getDuplicateFlightsData(){
        List<Flight> flights = new ArrayList<>();

        flights.add(new Flight()
                .setAid(1)
                .setSourceStation("Москва")
                .setDestStation("Питер")
                .setLoaded("Груж")
                .setCarNumber(123)
                .setInvNumber("T123")
                .setSendDate(Timestamp.valueOf(LocalDateTime.of(2022, Month.APRIL, 5, 0, 0)))
                .setArriveToDestStationDate(Timestamp.valueOf(LocalDateTime.of(2022, Month.APRIL, 15, 0, 0)))
                .setNextFlightStartDate(Timestamp.valueOf(LocalDateTime.of(2022, Month.APRIL, 21, 0, 0)))
        );

        flights.add(new Flight()
                .setAid(2)
                .setSourceStation("Питер")
                .setDestStation("Воронеж")
                .setLoaded("Груж")
                .setCarNumber(123)
                .setInvNumber("T123")
                .setSendDate(Timestamp.valueOf(LocalDateTime.of(2022, Month.APRIL, 5, 0, 0)))
                .setArriveToDestStationDate(Timestamp.valueOf(LocalDateTime.of(2022, Month.APRIL, 17, 0, 0)))
                .setNextFlightStartDate(Timestamp.valueOf(LocalDateTime.of(2022, Month.APRIL, 25, 0, 0)))
        );

        flights.add(new Flight()
                .setAid(3)
                .setSourceStation("Питер")
                .setDestStation("Воронеж")
                .setLoaded("Груж")
                .setCarNumber(123)
                .setInvNumber("T123")
                .setSendDate(Timestamp.valueOf(LocalDateTime.of(2022, Month.APRIL, 6, 0, 0)))
                .setArriveToDestStationDate(Timestamp.valueOf(LocalDateTime.of(2022, Month.APRIL, 18, 0, 0)))
                .setNextFlightStartDate(Timestamp.valueOf(LocalDateTime.of(2022, Month.APRIL, 28, 0, 0)))
        );

//        flights.add(new Flight()
//                .setAid(3)
//                .setSourceStation("Питер")
//                .setDestStation("Воронеж")
//                .setLoaded("Груж")
//                .setCarNumber(123)
//                .setInvNumber("T124")
//                .setSendDate(Timestamp.valueOf(LocalDateTime.of(2022, Month.APRIL, 6, 0, 0)))
//                .setArriveToDestStationDate(Timestamp.valueOf(LocalDateTime.of(2022, Month.APRIL, 19, 0, 0)))
//                .setNextFlightStartDate(Timestamp.valueOf(LocalDateTime.of(2022, Month.APRIL, 29, 0, 0)))
//        );

        return flights;
    }

    private Integer getDaysBetween(Timestamp t1, Timestamp t2){
        LocalDate d1 = t1.toLocalDateTime().toLocalDate();
        LocalDate d2 = t2.toLocalDateTime().toLocalDate();
        return (int) ChronoUnit.DAYS.between(d1, d2);
    }
}