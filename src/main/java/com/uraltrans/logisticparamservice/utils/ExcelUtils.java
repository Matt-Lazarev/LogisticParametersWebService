package com.uraltrans.logisticparamservice.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Component
public class ExcelUtils {
    public static List<String> extractHeaders(String path) {
        try {
            XSSFWorkbook wb = new XSSFWorkbook(Files.newInputStream(Paths.get(path)));
            XSSFSheet sheet = wb.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            Row row = rowIterator.next();

            Iterator<Cell> cellIterator = row.cellIterator();
            List<String> headers = new ArrayList<>();
            Cell cell;
            while (cellIterator.hasNext()) {
                cell = cellIterator.next();
                String header = cell.getStringCellValue();
                headers.add(header);
            }
            return headers;
        } catch (
                Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static List<List<String>> read(String path, Set<Integer> indexes) {
        try {
            XSSFWorkbook wb = new XSSFWorkbook(Files.newInputStream(Paths.get(path)));
            XSSFSheet sheet = wb.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            Row row;
            Cell cell;
            List<List<String>> excelData = new ArrayList<>();
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                List<String> rowData = new ArrayList<>();
                int cellIndex = 0;
                while (cellIterator.hasNext()) {
                    cell = cellIterator.next();
                    if(!indexes.contains(cellIndex++)){
                        continue;
                    }
                    String value = "";
                    switch (cell.getCellType()) {
                        case BOOLEAN:
                            value = "" + cell.getBooleanCellValue();
                            break;
                        case NUMERIC:
                            value = "" + cell.getNumericCellValue();
                            break;
                        case STRING:
                            value = "" + cell.getStringCellValue();
                            break;
                        case BLANK:
                            value = "";
                            break;
                        default:
                            break;
                    }
                    rowData.add(value);
                }
                excelData.add(rowData);
            }
            return excelData;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
