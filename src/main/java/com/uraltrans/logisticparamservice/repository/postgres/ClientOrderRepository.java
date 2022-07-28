package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.ClientOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {
    @Modifying
    @Transactional
    @Query(value = "truncate table client_orders restart identity", nativeQuery = true)
    void truncate();
}
