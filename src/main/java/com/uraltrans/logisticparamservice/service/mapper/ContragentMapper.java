package com.uraltrans.logisticparamservice.service.mapper;

import com.uraltrans.logisticparamservice.entity.postgres.Contragent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ContragentMapper {
    public List<Contragent> mapToContragentList(List<Map<String, Object>> data){
        return data
                .stream()
                .map(this::mapToContragent)
                .collect(Collectors.toList());
    }

    private Contragent mapToContragent(Map<String, Object> object){
        return Contragent.builder()
                .name((String) object.get("Name"))
                .email((String) object.get("Email"))
                .company((String) object.get("Company"))
                .INN((String) object.get("INN"))
                .type((String) object.get("Type"))
                .build();
    }
}
