package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.FlightAddressing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FlightAddressingRepository extends JpaRepository<FlightAddressing, Long> {

    @Query(value =
            """
            SELECT fr.destination_station_code, fr.source_station_code, fr.requirement_orders, fr.in_plan_orders
            FROM flight_requirements fr
            INNER JOIN station_handbook sh
                ON fr.source_station_code = sh.code6
            WHERE
                (SELECT DISTINCT lower(split_part(sh.region, ' ', 1)) FROM station_handbook sh WHERE sh.code6 = :stationCode) =
                lower(split_part(sh.region, ' ', 1))
            AND :volume BETWEEN fr.volume_from AND fr.volume_to
            """,
            nativeQuery = true)
    List<Map<String, Object>> findAllInRegion(String stationCode, BigDecimal volume);

    @Query("SELECT f FROM FlightAddressing f WHERE f.tariffId IN :ids")
    List<FlightAddressing> findAllByTariffId(List<Integer> ids);

    @Query("SELECT f FROM FlightAddressing f WHERE f.rateId IN :ids")
    List<FlightAddressing> findAllByRateId(List<Integer> ids);

    @Modifying
    @Transactional
    @Query("UPDATE FlightAddressing fa SET fa.tariff = :tariff WHERE fa.tariffId = :id")
    void updateTariffById(Integer id, BigDecimal tariff);

    @Modifying
    @Transactional
    @Query("UPDATE FlightAddressing fa SET fa.rate = :rate WHERE fa.rateId = :id")
    void updateRateById(Integer id, BigDecimal rate);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE flight_addressings RESTART IDENTITY", nativeQuery = true)
    void truncate();
}
