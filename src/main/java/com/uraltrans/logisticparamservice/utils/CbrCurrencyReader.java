package com.uraltrans.logisticparamservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uraltrans.logisticparamservice.dto.currency.CurrenciesDto;
import com.uraltrans.logisticparamservice.dto.currency.CurrencyDto;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CbrCurrencyReader {
    private static final String CBR_URL = "https://www.cbr-xml-daily.ru/daily_json.js";

    public Map<String, CurrencyDto> getAllCbrCurrencies() {
        try {
            URL url = new URL(CBR_URL);
            CurrenciesDto currenciesDto = new ObjectMapper().readValue(url, CurrenciesDto.class);
            return currenciesDto.getValute()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            e -> e.getValue().getNumCode(),
                            Map.Entry::getValue
                    ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
