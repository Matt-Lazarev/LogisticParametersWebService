package com.uraltrans.logisticparamservice.utils;

import com.uraltrans.logisticparamservice.dto.ActionLog;
import com.uraltrans.logisticparamservice.entity.postgres.Flight;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.*;

@Slf4j
public class FileUtils {
    public static final String DELIMITER = "   ";
    private static final Path DEFAULT_LOG_FILE_PATH = Paths.get("logging/action_logs.log");
    private static final int MAX_LOGS_AMOUNT = 20;

    private static final Path DEFAULT_DISCARDED_FLIGHTS_FILE_PATH = Paths.get("logging/discarded_flights.log");

    public static void writeActionLog(String message){
        try {
            Files.write(DEFAULT_LOG_FILE_PATH, Collections.singletonList(message), CREATE, APPEND);
        } catch (IOException e) {
            log.error("FileUtils write error: {}, {}", e.getMessage(), e.getStackTrace());
        }
    }

    public static List<ActionLog> readAllLogs(){
        List<ActionLog> logs = new ArrayList<>();
        try {
            Files.readAllLines(DEFAULT_LOG_FILE_PATH)
                    .stream()
                    .map(str ->str.split(DELIMITER))
                    .map(arr -> new ActionLog(arr[0], arr[1], arr[2]))
                    .forEach(logs::add);
        } catch (IOException e) {
            log.error("FileUtils read error: {}, {}", e.getMessage(), e.getStackTrace());
        }
        Collections.reverse(logs);
        return logs.size() <= MAX_LOGS_AMOUNT ? logs : logs.subList(0, MAX_LOGS_AMOUNT);
    }

    public static void writeDiscardedFlights(List<String> discardedFlights) {
        try {
            Files.write(DEFAULT_DISCARDED_FLIGHTS_FILE_PATH, discardedFlights);
        } catch (IOException e) {
            log.error("FileUtils write error: {}, {}", e.getMessage(), e.getStackTrace());
        }
    }

    public static List<String> readDiscardedFlights(){
        try {
            return Files.readAllLines(DEFAULT_DISCARDED_FLIGHTS_FILE_PATH);
        } catch (IOException e) {
            log.error("FileUtils read error: {}, {}", e.getMessage(), e.getStackTrace());
        }
        return new ArrayList<>();
    }
}
