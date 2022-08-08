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
            "select fr.destination_station_code, fr.source_station_code, fr.requirement_orders " +
                    "from flight_requirements fr " +
                    "         inner join station_handbook sh " +
                    "         on fr.destination_station_code = sh.code6 " +
                    "where (select distinct split_part(sh.region, ' ', 1) from station_handbook sh " +
                    "       where sh.code6 = :stationCode) = split_part(sh.region, ' ', 1)", nativeQuery = true)
    List<Map<String, Object>> findAllInRegion(String stationCode);

    @Query("update FlightAddressing fa set fa.tariff = :tariff where fa.id = :id")
    void updateTariffById(Long id, BigDecimal tariff);

    @Query("update FlightAddressing fa set fa.rate = :rate where fa.id = :id")
    void updateRateById(Long id, BigDecimal rate);

    @Modifying
    @Transactional
    @Query(value = "truncate table flight_addressings restart identity", nativeQuery = true)
    void truncate();
}
