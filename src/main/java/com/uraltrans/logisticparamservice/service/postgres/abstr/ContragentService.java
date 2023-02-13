package com.uraltrans.logisticparamservice.service.postgres.abstr;

import com.uraltrans.logisticparamservice.entity.postgres.Contragent;

import java.util.List;

public interface ContragentService {
    void saveAllContragents();

    List<Contragent> getAllContragents();
}
