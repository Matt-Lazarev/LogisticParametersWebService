package com.uraltrans.logisticparamservice.service.postgres.abstr;


import com.uraltrans.logisticparamservice.entity.postgres.NoDetailsWagon;

import java.util.List;

public interface NoDetailsWagonService {
    List<NoDetailsWagon> getAllNoDetailsWagon();

    void saveAllNoDetailsWagon();
}
