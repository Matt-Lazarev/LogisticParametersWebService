package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.dto.planfact.OrdersDto;
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
            """
            SELECT sub_co.volume_from, sub_co.volume_to,sub_co.source_station_code6, sub_co.destination_station_code6, sub_co.id,
                sum(sub_co.cars_amount) AS cars_amount,
                sum(sub_co.completed_orders) AS completed_orders,
                sum(sub_co.in_progress_orders) AS in_progress_orders,
                avg(sub_co.ut_rate) AS ut_rate
            FROM
                (SELECT co.volume_from, co.volume_to,co.source_station_code6, co.destination_station_code6,
                co.cars_amount AS cars_amount, co.ut_rate AS ut_rate, co.id,
                sum(af_sub.completed_orders) AS completed_orders,
                sum(af_sub.in_progress_orders) AS in_progress_orders
                FROM client_orders co
                LEFT JOIN
                    (SELECT af.volume, af.source_station_code, af.destination_station_code,
                    sum(af.completed_orders) AS completed_orders, sum(af.in_progress_orders) AS in_progress_orders
                    FROM actual_flights af
                    WHERE af.source_station_code != af.destination_station_code
                    GROUP BY af.volume, af.source_station_code, af.destination_station_code) af_sub
                ON co.source_station_code6 = af_sub.source_station_code AND af_sub.volume BETWEEN co.volume_from AND co.volume_to
                GROUP BY co.volume_from, co.volume_to,co.source_station_code6, co.destination_station_code6, co.cars_amount, co.id, co.ut_rate) AS sub_co
            GROUP BY sub_co.volume_from, sub_co.volume_to, sub_co.source_station_code6, sub_co.destination_station_code6, sub_co.id
            """,
            nativeQuery = true)
    List<Map<String, Object>> groupActualFlightsAndClientOrders();

    @Query("""
           SELECT new com.uraltrans.logisticparamservice.dto.planfact.OrdersDto
           (fr.requirementOrders, fr.utRate)
           FROM FlightRequirement fr
           WHERE :volume BETWEEN fr.volumeFrom AND fr.volumeTo
               AND fr.sourceStationCode = :sourceStationCode
               AND fr.destinationStationCode = :destinationStationCode
           """)
    OrdersDto findRequirementByVolumeAndStationCodes(BigDecimal volume, String sourceStationCode, String destinationStationCode);

    @Query("""
           SELECT new com.uraltrans.logisticparamservice.dto.planfact.OrdersDto(SUM(fr.requirementOrders), AVG(fr.utRate))
           FROM FlightRequirement fr
           WHERE :volume BETWEEN fr.volumeFrom AND fr.volumeTo
                 AND fr.sourceStationCode = :sourceStationCode
           GROUP BY fr.sourceStationCode
           """)
    OrdersDto findAllRequirementByVolumeAndSourceStationCode(BigDecimal volume, String sourceStationCode);

    @Query("SELECT fr.sourceStationCode FROM FlightRequirement fr ")
    List<String> findAllSourceStationCodes();

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE flight_requirements RESTART IDENTITY", nativeQuery = true)
    void truncate();
}
