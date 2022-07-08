package com.uraltrans.logisticparamservice.utils.mapper;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.util.Map;
import java.util.function.Function;

public interface ExcelRowMapper<T> {
    <S> T mapRowToObject(Class<T> clazz, Row row, Row headersRow,
                    Map<Class<S>, Function<? super CharSequence, S>> unknownTypeMappers) throws Exception;
    <S> void mapObjectToRow(T object, Row row, CellStyle style,
                        Map<Class<S>, Function<S, ? super CharSequence>> unknownFieldTypeMapper) throws Exception;
}
