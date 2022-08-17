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
            "select fr.destination_station_code, fr.source_station_code, fr.requirement_orders, fr.in_plan_orders " +
                    "from flight_requirements fr " +
                    "         inner join station_handbook sh " +
                    "         on fr.source_station_code = sh.code6 " +
                    "where (select distinct lower(split_part(sh.region, ' ', 1)) from station_handbook sh " +
                    "       where sh.code6 = :stationCode) = lower(split_part(sh.region, ' ', 1)) " +
                    "and :volume between fr.volume_from and fr.volume_to", nativeQuery = true)
    List<Map<String, Object>> findAllInRegion(String stationCode, BigDecimal volume);

    @Modifying
    @Transactional
    @Query("update FlightAddressing fa set fa.tariff = :tariff where fa.id = :id")
    void updateTariffById(Long id, BigDecimal tariff);

    @Modifying
    @Transactional
    @Query("update FlightAddressing fa set fa.rate = :rate where fa.id = :id")
    void updateRateById(Long id, BigDecimal rate);

    @Modifying
    @Transactional
    @Query(value = "truncate table flight_addressings restart identity", nativeQuery = true)
    void truncate();
}
