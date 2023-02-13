package com.uraltrans.logisticparamservice.utils;

import org.apache.commons.io.Charsets;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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

    public static void writeCsvFile(String directory, String filename, List<Map<String, Object>> data){
        try{
            Path dirPath = Paths.get(directory);
            if(Files.notExists(dirPath)){
                Files.createDirectory(dirPath);
            }

            Path fullFilenamePath = Paths.get(directory + "/" + filename);
            if(data.size() == 0){
                Files.write(fullFilenamePath, Collections.emptyList());
                return;
            }

            String headers = String.join(",", new ArrayList<>(data.get(0).keySet()));

            List<String> rows = data
                    .stream()
                    .map(Map::values)
                    .map(row -> row.stream().map(String::valueOf).collect(Collectors.joining(",")))
                    .map(row -> row.replaceAll("[\n\r]+", ""))
                    .collect(Collectors.toList());

            List<String> allRows = new ArrayList<>();
            allRows.add(headers);
            allRows.addAll(rows);

            Files.write(fullFilenamePath, allRows);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getFileData(String directory, String filename){
        try{
            Path fullFilenamePath = Paths.get(directory + "/" + filename);

            return Files.readAllBytes(fullFilenamePath);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
