package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.dto.cargo.CargoDto;
import com.uraltrans.logisticparamservice.entity.postgres.ClientOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {
    @Query("""
           SELECT new com.uraltrans.logisticparamservice.dto.cargo.CargoDto(co.cargoCode, co.utRate)
           FROM ClientOrder co
           WHERE co.sourceStationCode6 = :sourceStation AND :volume BETWEEN co.volumeFrom AND co.volumeTo
           GROUP BY co.sourceStationCode6, co.cargoCode, co.utRate
           """)
    List<CargoDto> findBySourceStationCodeAndVolume(String sourceStation, BigDecimal volume);

    @Query("""
            SELECT AVG(co.utRate)
            FROM ClientOrder co
            WHERE co.sourceStationCode6 = :sourceStation
                AND co.destinationStationCode6 = :destStation
                AND :volume BETWEEN co.volumeFrom AND co.volumeTo
            GROUP BY co.sourceStationCode6, co.destinationStationCode6
           """)
    BigDecimal findUtRateByStationCodesAndVolume(String sourceStation, String destStation, BigDecimal volume);

    @Query("""
            SELECT AVG(co.utRate)
            FROM ClientOrder co
            WHERE co.sourceStationCode6 = :sourceStation
                AND co.destinationStationCode6 IS NULL
                AND :volume BETWEEN co.volumeFrom AND co.volumeTo
            GROUP BY co.sourceStationCode6, co.destinationStationCode6
           """)
    BigDecimal findUtRateBySourceStationCodeAndVolume(String sourceStation, BigDecimal volume);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE client_orders RESTART IDENTITY", nativeQuery = true)
    void truncate();


}
