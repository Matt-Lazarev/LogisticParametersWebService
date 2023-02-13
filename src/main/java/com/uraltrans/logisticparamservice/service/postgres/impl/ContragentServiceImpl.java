package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.entity.postgres.Contragent;
import com.uraltrans.logisticparamservice.repository.itr.ItrContragentRepository;
import com.uraltrans.logisticparamservice.repository.postgres.ContragentRepository;
import com.uraltrans.logisticparamservice.service.mapper.ContragentMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ContragentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContragentServiceImpl implements ContragentService {
    private static final String CONTRAGENT_TYPE = "Ответственный за предоставление документов по НДС 0%";

    private final ItrContragentRepository itrContragentRepository;
    private final ContragentRepository contragentRepository;
    private final ContragentMapper contragentMapper;

    @Override
    public void saveAllContragents() {
        prepareNextSave();

        List<Map<String, Object>> contragentsRawData = itrContragentRepository.getAllContragentsByType(CONTRAGENT_TYPE);
        List<Contragent> contragents = contragentMapper.mapToContragentList(contragentsRawData);

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
