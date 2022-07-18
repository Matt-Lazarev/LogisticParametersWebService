package com.uraltrans.logisticparamservice.utils.excel;

import com.uraltrans.logisticparamservice.utils.mapper.SimpleExcelRowMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ExcelReaderWriterService<T> {

    private final SimpleExcelRowMapper<T> excelRowMapper;

    public ExcelReaderWriterService(SimpleExcelRowMapper<T> excelRowMapper) {
        this.excelRowMapper = excelRowMapper;
    }

    public <S> List<T> readAsList(String fileName, Class<T> clazz,
                                  Map<Class<S>, Function<? super CharSequence, S>> unknownFieldTypeMapper) {
        List<T> objects = new ArrayList<>();
        File file = new File(fileName);
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            for (Sheet sh : workbook) {
                Iterator<Row> it = sh.iterator();
                Row headerRow = it.next();
                while (it.hasNext()) {
                    Row row = it.next();
                    T object = excelRowMapper.mapRowToObject(clazz, row, headerRow, unknownFieldTypeMapper);
                    objects.add(object);
                }
            }
            return objects;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <S> void write(String fileName, String sheetName, List<String> headers, List<T> data,
                          Map<Class<S>, Function<S, ? super CharSequence>> unknownFieldTypeMapper) {
        File file = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet(sheetName);

            int rowIndex = 0;
            if (headers != null) {
                Row header = sheet.createRow(rowIndex++);
                CellStyle headerStyle = workbook.createCellStyle();
                XSSFFont font = workbook.createFont();
                font.setFontHeightInPoints((short) 16);
                font.setBold(true);
                headerStyle.setFont(font);

                for (int i = 0; i < headers.size(); i++) {
                    Cell headerCell = header.createCell(i);
                    headerCell.setCellValue(headers.get(i));
                    headerCell.setCellStyle(headerStyle);
                }
            }

            CellStyle style = workbook.createCellStyle();
            for (T object : data) {
                Row row = sheet.createRow(rowIndex++);
                excelRowMapper.mapObjectToRow(object, row, style, unknownFieldTypeMapper);
            }

            workbook.write(fos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
