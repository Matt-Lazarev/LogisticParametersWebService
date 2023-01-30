package com.uraltrans.logisticparamservice.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class CsvUtils {
    private static final String DEFAULT_DELIMITER = ";";

    public static List<String[]> readCsvFile(String filename){
       return readCsvFile(filename, DEFAULT_DELIMITER);
    }

    public static List<String[]> readCsvFile(String filename, String delimiter){
        try {
            return Files.readAllLines(Paths.get(filename))
                    .stream()
                    .skip(1)
                    .map(row -> row.split(delimiter))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
