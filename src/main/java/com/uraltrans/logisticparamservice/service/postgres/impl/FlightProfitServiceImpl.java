package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.currency.CurrencyDto;
import com.uraltrans.logisticparamservice.entity.postgres.FlightProfit;
import com.uraltrans.logisticparamservice.repository.postgres.FlightProfitRepository;
import com.uraltrans.logisticparamservice.service.currency.CbrCurrencyReader;
import com.uraltrans.logisticparamservice.service.mapper.FlightProfitMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightProfitService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightTimeDistanceService;
import com.uraltrans.logisticparamservice.service.utcsrs.RawFlightProfitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightProfitServiceImpl implements FlightProfitService {
    private final RawFlightProfitService rawFlightProfitService;
    private final FlightTimeDistanceService flightTimeDistanceService;
    private final FlightProfitRepository flightProfitRepository;
    private final FlightProfitMapper flightProfitMapper;
    private final CbrCurrencyReader cbrCurrencyReader;

    @Override
    @Transactional(readOnly = true)
    public List<FlightProfit> getAllFlightProfits() {
        return flightProfitRepository.findAll();
    }

    @Override
    @Transactional
    public void saveAll() {
        prepareNextSave();
        List<FlightProfit> flightProfits = flightProfitMapper.mapToList(rawFlightProfitService.getAll());

        calculateProfit(flightProfits);
        flightProfits = filterFlights(flightProfits);
        calculateAverageTravelTime(flightProfits);

        flightProfitRepository.saveAll(flightProfits);
    }

    private void prepareNextSave() {
        flightProfitRepository.truncate();
    }

    private List<FlightProfit> filterFlights(List<FlightProfit> flightProfits) {
        return flightProfits
                .stream()
                .filter(f -> f.getDestStationCode() != null && f.getSourceStationCode() != null)
                .filter(f -> f.getCargo() != null && f.getCargoCode() != null && f.getVolume() != null)
                .filter(f -> f.getProfit() != null && f.getProfit().doubleValue() > 0)
                .collect(Collectors.toList());
    }


    private void calculateProfit(List<FlightProfit> flightProfits) {
        Map<String, CurrencyDto> currencies = cbrCurrencyReader.getAllCbrCurrencies();
        flightProfits
                .stream()
                .filter(f -> f.getCurrencyProfit() != null && f.getCurrencyProfit().doubleValue() > 0)
                .filter(f -> f.getCurrency() != null)
                .forEach(f -> f.setProfit(convert(f.getCurrencyProfit(), f.getCurrency(), currencies)));
    }

    private BigDecimal convert(BigDecimal profit, String currencyNumCode, Map<String, CurrencyDto> currencies) {
        CurrencyDto currency = currencies.get(currencyNumCode);
        if(currency == null){
            return profit;
        }
        return profit.multiply(currency.getValue());
    }

    private void calculateAverageTravelTime(List<FlightProfit> flightProfits) {
        for(FlightProfit flightProfit : flightProfits){
            String sourceStationCode = flightProfit.getSourceStationCode();
            String destStationCode= flightProfit.getDestStationCode();
            flightTimeDistanceService.findByStationCodesAndFlightType(sourceStationCode, destStationCode, "Груженый")
                    .ifPresent(f -> flightProfit.setAverageTravelTime(new BigDecimal(f.getTravelTime())));
        }
    }
}
