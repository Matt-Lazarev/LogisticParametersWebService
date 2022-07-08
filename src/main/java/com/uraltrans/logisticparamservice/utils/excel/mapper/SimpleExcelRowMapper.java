package com.uraltrans.logisticparamservice.utils.excel.mapper;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleExcelRowMapper<T> implements ExcelRowMapper<T> {

    public <S> T mapRowToObject(Class<T> clazz, Row row, Row headersRow,
                                Map<Class<S>, Function<? super CharSequence, S>> mappers) throws Exception {
        List<Field> fields = claimFields(headersRow, clazz);

        T object = clazz.getDeclaredConstructor().newInstance();

        int index = 0;
        for (Cell cell : row) {
            if(fields.size() == 0){
                break;
            }
            Field field = fields.get(index++);
            setValueToObjectField(field, object, cell, mappers);
        }
        return object;
    }

    public <S> void mapObjectToRow(T object, Row row, CellStyle style,
                                   Map<Class<S>, Function<S, ? super CharSequence>> mappers) throws Exception {
        List<Field> fields = claimFields(object.getClass());

        for (int i = 0; i < fields.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
            Field field = fields.get(i);
            field.setAccessible(true);
            setObjectValueToCell(cell, field.get(object), mappers);
        }
    }

    private List<Field> claimFields(Row row, Class<?> clazz) {
        DataFormatter dataFormatter = new DataFormatter();
        List<String> headers = new ArrayList<>();
        for (Cell cell : row) {
            headers.add(dataFormatter.formatCellValue(cell));
        }
        return headers.stream()
                .filter(field -> field != null && !field.isEmpty())
                .map(field -> String.valueOf(field.charAt(0)).toLowerCase() + field.substring(1))
                .map(field -> {
                    try {
                        return clazz.getDeclaredField(field);
                    } catch (NoSuchFieldException ignored) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Field> claimFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(field -> {
                    try {
                        return clazz.getDeclaredField(field.getName());
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .collect(Collectors.toList());
    }

    private <S> void setValueToObjectField(Field field, Object object, Cell cell,
                                           Map<Class<S>, Function<? super CharSequence, S>> mappers) throws Exception {
        field.setAccessible(true);

        Class<?> fieldType = field.getType();
        if (fieldType == byte.class || fieldType == Byte.class)
            field.set(object, (byte) cell.getNumericCellValue());
        else if (fieldType == short.class || fieldType == Short.class)
            field.set(object, (short) cell.getNumericCellValue());
        else if (fieldType == int.class || fieldType == Integer.class)
            field.set(object, (int) cell.getNumericCellValue());
        else if (fieldType == long.class || fieldType == Long.class)
            field.set(object, (long) cell.getNumericCellValue());
        else if (fieldType == float.class || fieldType == Float.class)
            field.set(object, (float) cell.getNumericCellValue());
        else if (fieldType == double.class || fieldType == Double.class)
            field.set(object, cell.getNumericCellValue());
        else if (fieldType == char.class || fieldType == Character.class)
            field.set(object, (char) cell.getNumericCellValue());
        else if (fieldType == boolean.class || fieldType == Boolean.class)
            field.set(object, cell.getBooleanCellValue());
        else if (fieldType == String.class)
            field.set(object, cell.getStringCellValue());
        else if (fieldType == LocalDateTime.class)
            field.set(object, cell.getLocalDateTimeCellValue());
        else if (fieldType == LocalDate.class){
            if(cell.getLocalDateTimeCellValue() != null){
                field.set(object, cell.getLocalDateTimeCellValue().toLocalDate());
            }
        }
        else if (fieldType == LocalTime.class)
            field.set(object, cell.getLocalDateTimeCellValue().toLocalTime());
        else if (fieldType == Date.class) {
            Date date = Date.from(cell.getLocalDateTimeCellValue()
                    .toLocalDate()
                    .atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant());
            field.set(object, date);
        } else if (fieldType == java.sql.Date.class) {
            java.sql.Date date = java.sql.Date.valueOf(cell.getLocalDateTimeCellValue().toLocalDate());
            field.set(object, date);
        } else if (fieldType == BigDecimal.class) {
            double cellValue = cell.getNumericCellValue();
            field.set(object, BigDecimal.valueOf(cellValue).setScale(3, RoundingMode.HALF_UP));
        }
        else {
            Function<? super CharSequence, S> mapper = mappers.get(fieldType);
            if(mapper != null){
                field.set(object, mapper.apply(cell.getStringCellValue()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <S> void setObjectValueToCell(Cell cell, Object object,
                                      Map<Class<S>, Function<S, ? super CharSequence>> mappers) {
        if(object == null){
            return;
        }

        Class<?> fieldType = object.getClass();
        if (fieldType == Byte.class)
            cell.setCellValue((byte) object);
        else if (fieldType == Short.class)
            cell.setCellValue((short) object);
        else if (fieldType == Integer.class)
            cell.setCellValue((int) object);
        else if (fieldType == Long.class)
            cell.setCellValue((long) object);
        else if (fieldType == Float.class)
            cell.setCellValue((float) object);
        else if (fieldType == Double.class)
            cell.setCellValue((double) object);
        else if (fieldType == Character.class)
            cell.setCellValue((char) object);
        else if (fieldType == Boolean.class)
            cell.setCellValue((boolean) object);
        else if (fieldType == String.class)
            cell.setCellValue((String) object);
        else if (fieldType == LocalDateTime.class)
            cell.setCellValue(((LocalDateTime) object).toString());
        else if (fieldType == LocalDate.class)
            cell.setCellValue(((LocalDate) object).toString());
        else if (fieldType == Date.class) {
            cell.setCellValue(java.sql.Date.valueOf(cell.getLocalDateTimeCellValue().toLocalDate()).toString());
        } else if (fieldType == java.sql.Date.class) {
            cell.setCellValue(java.sql.Date.valueOf(cell.getLocalDateTimeCellValue().toLocalDate()).toString());
        } else if (fieldType == BigDecimal.class) {
            cell.setCellValue(((BigDecimal) object).doubleValue());
        }
        else {
            Function<S, ? super CharSequence> mapper = mappers.get(fieldType);
            if(mapper != null){
                cell.setCellValue(mapper.apply((S) object).toString());
            }
        }
    }
}
