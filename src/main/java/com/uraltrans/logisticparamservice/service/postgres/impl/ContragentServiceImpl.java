package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.itr.projection.ItrContagentProjection;
import com.uraltrans.logisticparamservice.entity.postgres.Contragent;
import com.uraltrans.logisticparamservice.repository.itr.ItrContragentRepository;
import com.uraltrans.logisticparamservice.repository.postgres.ContragentRepository;
import com.uraltrans.logisticparamservice.service.mapper.mapstruct.ItrContagentMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ContragentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContragentServiceImpl implements ContragentService {
    private static final String CONTRAGENT_TYPE = "Ответственный за предоставление документов по НДС 0%";

    private final ItrContragentRepository itrContragentRepository;
    private final ContragentRepository contragentRepository;
    private final ItrContagentMapper itrContagentMapper;

    @Override
    public void saveAllContragents() {
        prepareNextSave();

        List<ItrContagentProjection> itrContragents = itrContragentRepository.getAllItrContragents(CONTRAGENT_TYPE);
        List<Contragent> contragents = itrContagentMapper.toContragentList(itrContragents);

        contragentRepository.saveAll(contragents);
    }

    @Override
    public List<Contragent> getAllContragents() {
        return contragentRepository.findAll();
    }

    private void prepareNextSave(){
        contragentRepository.truncate();
    }
}
