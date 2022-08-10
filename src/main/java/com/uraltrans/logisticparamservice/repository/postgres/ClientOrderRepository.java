package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.ClientOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {

    //source_station_code6, destination_station_code6, volume_from, volume_to, cargo_code from client_orders
    //group by source_station_code6, destination_station_code6, volume_from, volume_to, cargo_code

    @Query("select co.cargoCode " +
           "from ClientOrder co " +
           "where co.sourceStationCode6 = :sourceStation and " +
           "      :volume between co.volumeFrom and co.volumeTo " +
           "group by co.sourceStationCode6, co.volumeFrom, co.volumeTo, co.cargoCode")
    List<String> findByStationCodesAndVolume(String sourceStation, BigDecimal volume);

    @Modifying
    @Transactional
    @Query(value = "truncate table client_orders restart identity", nativeQuery = true)
    void truncate();
}
