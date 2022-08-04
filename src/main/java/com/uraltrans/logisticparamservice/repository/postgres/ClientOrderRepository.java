package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.ClientOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigDecimal;

public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {

    @Query("select co " +
           "from ClientOrder co " +
           "where co.sourceStationCode6 = :sourceStation and " +
           "      co.destinationStationCode6 = :destStation and " +
           "      co.volumeFrom = :volumeFrom and " +
           "      co.volumeTo = :volumeTo")
    ClientOrder findByStationCodesAndVolume(String sourceStation, String destStation, BigDecimal volumeFrom, BigDecimal volumeTo);
    @Modifying
    @Transactional
    @Query(value = "truncate table client_orders restart identity", nativeQuery = true)
    void truncate();
}
