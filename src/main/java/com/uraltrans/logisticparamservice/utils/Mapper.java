package com.uraltrans.logisticparamservice.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Mapper {
    private static final int SHIFT_1C_YEARS = 2000;

    public static LocalDate fix1cDate(LocalDate date){
        return date.getYear() == SHIFT_1C_YEARS ? null : date.minusYears(SHIFT_1C_YEARS);
    }

    public static LocalDateTime fix1cDate(Timestamp timestamp){
        LocalDateTime date = timestamp.toLocalDateTime();
        return date.getYear() == SHIFT_1C_YEARS ? null : date.minusYears(SHIFT_1C_YEARS);
    }

    public static LocalDate toLocalDate(Timestamp timestamp){
        return timestamp != null ? timestamp.toLocalDateTime().toLocalDate() : null;
    }

    public static BigDecimal toBigDecimal(Double number){
        return number != null ? BigDecimal.valueOf(number) : null;
    }

    public static String round(double num, int decimalPoints){
        return BigDecimal.valueOf(num).setScale(decimalPoints, RoundingMode.HALF_UP).toString();
    }

    public static Integer toInteger(BigDecimal number) {
        return number != null ? number.intValue() : 0;
    }

    public static Integer toInteger(BigInteger number) {
        return number != null ? number.intValue() : 0;
    }

    public static LocalDate to1cDate(LocalDate date) {
        return date.plusYears(SHIFT_1C_YEARS);
    }
}
