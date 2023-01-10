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
    public static List<List<String>> read(String path) {
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
                while (cellIterator.hasNext()) {
                    cell = cellIterator.next();
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
