package com.uraltrans.logisticparamservice.service.postgres.impl;

import com.uraltrans.logisticparamservice.dto.currency.CurrencyDto;
import com.uraltrans.logisticparamservice.entity.postgres.FlightProfit;
import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.repository.postgres.FlightProfitRepository;
import com.uraltrans.logisticparamservice.repository.utcsrs.UtcsrsFlightProfitRepository;
import com.uraltrans.logisticparamservice.utils.CbrCurrencyReader;
import com.uraltrans.logisticparamservice.service.mapper.mapstruct.UtcsrsFlightProfitMapper;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightProfitService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightTimeDistanceService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import com.uraltrans.logisticparamservice.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightProfitServiceImpl implements FlightProfitService {
    private final LoadParameterService loadParameterService;
    private final FlightTimeDistanceService flightTimeDistanceService;
    private final FlightProfitRepository flightProfitRepository;
    private final UtcsrsFlightProfitRepository utcsrsFlightProfitRepository;
    private final UtcsrsFlightProfitMapper utcsrsFlightProfitMapper;
    private final CbrCurrencyReader cbrCurrencyReader;

    @Override
    public List<FlightProfit> getAllFlightProfits() {
        return flightProfitRepository.findAll();
    }

    @Override
    public void saveAll() {
        prepareNextSave();
        List<FlightProfit> flightProfits = loadFlightProfits();

        calculateProfit(flightProfits);
        flightProfits = filterFlights(flightProfits);
        calculateAverageTravelTime(flightProfits);

        flightProfitRepository.saveAll(flightProfits);
    }

    private void prepareNextSave() {
        flightProfitRepository.truncate();
    }

    private List<FlightProfit> loadFlightProfits(){
        LoadParameters params = loadParameterService.getLoadParameters();
        Integer daysToRetrieve = params.getFlightProfitDaysToRetrieveData();
        String fromDate = MappingUtils.to1cDate(LocalDate.now().minusDays(daysToRetrieve)).toString();
        return utcsrsFlightProfitMapper.toFlightProfitList(utcsrsFlightProfitRepository.getAllFlightProfits(fromDate));
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
                .filter(f -> f.getCurrency() != null)
                .forEach(f -> f.setProfit(convert(f.getProfit(), f.getCurrency(), currencies)));
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
