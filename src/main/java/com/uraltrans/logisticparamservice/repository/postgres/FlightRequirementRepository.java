package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.FlightRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FlightRequirementRepository extends JpaRepository<FlightRequirement, Long> {

    @Query(value =
            "select sub_co.volume_from, sub_co.volume_to,sub_co.source_station_code6, sub_co.destination_station_code6, " +
                    "       sum(sub_co.cars_amount) as cars_amount, " +
                    "       sum(sub_co.completed_orders) as completed_orders, " +
                    "       sum(sub_co.in_progress_orders) as in_progress_orders " +
                    "from " +
                    "(select co.volume_from, co.volume_to,co.source_station_code6, co.destination_station_code6, " +
                    "       co.cars_amount as cars_amount, " +
                    "       sum(af_sub.completed_orders) as completed_orders, " +
                    "       sum(af_sub.in_progress_orders) as in_progress_orders " +
                    "                    from client_orders co " +
                    "                    left join " +
                    "                    (select af.volume, af.source_station_code, af.destination_station_code, " +
                    "                            sum(af.completed_orders) as completed_orders, sum(af.in_progress_orders) as in_progress_orders " +
                    "                            from actual_flights af " +
                    "                            group by af.volume, af.source_station_code, af.destination_station_code) af_sub " +
                    "                    on co.source_station_code6 = af_sub.source_station_code and " +
                    "                       co.destination_station_code6 = af_sub.destination_station_code and " +
                    "                       af_sub.volume between co.volume_from and co.volume_to " +
                    "                    group by co.volume_from, co.volume_to,co.source_station_code6, co.destination_station_code6, " +
                    "                             co.cars_amount) sub_co " +
                    "group by sub_co.volume_from, sub_co.volume_to, sub_co.source_station_code6, sub_co.destination_station_code6;", nativeQuery = true)
    List<Map<String, Object>> groupActualFlightsAndClientOrders();

    @Modifying
    @Transactional
    @Query(value = "truncate table flight_requirements restart identity", nativeQuery = true)
    void truncate();

    @Query("select fr.requirementOrders " +
            "from FlightRequirement fr " +
            "where :volume between fr.volumeFrom and fr.volumeTo " +
            "and fr.sourceStationCode = :sourceStationCode " +
            "and fr.destinationStationCode = :destinationStationCode")
    Integer findRequirementByVolumeAndStationCodes(BigDecimal volume, String sourceStationCode, String destinationStationCode);

    @Query("select fr.sourceStationCode from FlightRequirement fr ")
    List<String> findAllSourceStationCodes();
}
