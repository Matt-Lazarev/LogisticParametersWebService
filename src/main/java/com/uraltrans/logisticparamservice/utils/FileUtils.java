package com.uraltrans.logisticparamservice.utils;

import com.uraltrans.logisticparamservice.dto.logs.ActionLog;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

@Slf4j
public class FileUtils {
    public static final String DELIMITER = "   ";
    private static final int MAX_ACTION_LOGS_AMOUNT = 50;
    private static final Path DEFAULT_ACTION_LOGS_FILE_PATH = Paths.get("logging/action_logs.log");
    private static final Path DEFAULT_REGION_SEGMENTATION_ACTION_LOGS_FILE_PATH = Paths.get("logging/region_segmentation_action_logs.log");
    private static final Path DEFAULT_LOGS_FILE_PATH = Paths.get("logging");

    private static final Path DEFAULT_DISCARDED_SECOND_EMPTY_FLIGHTS_FILE_PATH = Paths
            .get("logging/discarded_second_empty_flights.log");

    private static final Path DEFAULT_DISCARDED_FLIGHTS_FILE_PATH = Paths.get("logging/discarded_flights.log");
    private static final Path DEFAULT_TARIFF_RATE_ERRORS_FILE_PATH = Paths.get("logging/tariff_rate_errors.log");
    private static final Path DEFAULT_BACK_BUTTON_FILE_PATH = Paths.get("button/button-url.txt");

    private static final Path CLIENT_ORDERS_SQL_SCRIPT_PATH = Paths.get("sql/client_orders_script.sql");

    private static final Path TARIFFICATION_DIRECTORY_PATH = Paths.get("tariffication_data");

    static {
        try {
            if (!Files.exists(DEFAULT_ACTION_LOGS_FILE_PATH)) {
                Files.createFile(DEFAULT_ACTION_LOGS_FILE_PATH);
            }
            if (!Files.exists(DEFAULT_DISCARDED_FLIGHTS_FILE_PATH)) {
                Files.createFile(DEFAULT_DISCARDED_FLIGHTS_FILE_PATH);
            }
            if (!Files.exists(DEFAULT_DISCARDED_SECOND_EMPTY_FLIGHTS_FILE_PATH)) {
                Files.createFile(DEFAULT_DISCARDED_SECOND_EMPTY_FLIGHTS_FILE_PATH);
            }
            if (!Files.exists(DEFAULT_REGION_SEGMENTATION_ACTION_LOGS_FILE_PATH)) {
                Files.createFile(DEFAULT_REGION_SEGMENTATION_ACTION_LOGS_FILE_PATH);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void writeActionLog(String message) {
        try {
            synchronized (FileUtils.class) {
                Files.write(DEFAULT_ACTION_LOGS_FILE_PATH, Collections.singletonList(message), CREATE, APPEND);
            }
        }
        catch (IOException e) {
            log.error("FileUtils write error: {}, {}", e.getMessage(), e.getStackTrace());
        }
    }

    public static void writeRegionSegmentationActionLog(String message) {
        try {
            synchronized (FileUtils.class) {
                Files.write(DEFAULT_REGION_SEGMENTATION_ACTION_LOGS_FILE_PATH, Collections.singletonList(message), CREATE, APPEND);
            }
        }
        catch (IOException e) {
            log.error("FileUtils write error: {}, {}", e.getMessage(), e.getStackTrace());
        }
    }

    public static List<ActionLog> readAllLogs() {
        List<ActionLog> logs = new ArrayList<>();
        try {
            Files.readAllLines(DEFAULT_ACTION_LOGS_FILE_PATH)
                    .stream()
                    .map(str -> str.split(DELIMITER))
                    .map(arr -> new ActionLog(null, arr[0], arr[1], arr[2]))
                    .forEach(logs::add);
        }
        catch (IOException e) {
            log.error("FileUtils read error: {}, {}", e.getMessage(), e.getStackTrace());
        }
        Collections.reverse(logs);
        return logs.size() <= MAX_ACTION_LOGS_AMOUNT ? logs : logs.subList(0, MAX_ACTION_LOGS_AMOUNT);
    }

    public static List<ActionLog> readAllRegionSegmentationLogs() {
        List<ActionLog> logs = new ArrayList<>();
        try {
            Files.readAllLines(DEFAULT_REGION_SEGMENTATION_ACTION_LOGS_FILE_PATH)
                    .stream()
                    .map(str -> str.split(DELIMITER))
                    .map(arr -> new ActionLog(arr[0], arr[1], arr[2], arr[3]))
                    .forEach(logs::add);
        }
        catch (IOException e) {
            log.error("FileUtils read error: {}, {}", e.getMessage(), e.getStackTrace());
        }
        Collections.reverse(logs);
        return logs.size() <= MAX_ACTION_LOGS_AMOUNT ? logs : logs.subList(0, MAX_ACTION_LOGS_AMOUNT);
    }

    public static void writeDiscardedFlights(List<String> discardedFlights, boolean append) {
        writeList(discardedFlights, append, DEFAULT_DISCARDED_FLIGHTS_FILE_PATH);
    }

    public static void writeDiscardedSecondEmptyFlights(List<String> discardedFlights, boolean append) {
        writeList(discardedFlights, append, DEFAULT_DISCARDED_SECOND_EMPTY_FLIGHTS_FILE_PATH);
    }

    public static void writeTariffRateErrors(List<String> tariffRateErrors, boolean append) {
        writeList(tariffRateErrors, append, DEFAULT_TARIFF_RATE_ERRORS_FILE_PATH);
    }

    private static void writeList(List<String> data, boolean append, Path path) {
        try {
            synchronized (FileUtils.class) {
                if (!append) {
                    Files.write(path, data);
                } else {
                    Files.write(path, data, CREATE, APPEND);
                }
            }
        }
        catch (IOException e) {
            log.error("FileUtils write error: {}, {}", e.getMessage(), e.getStackTrace());
        }
    }

    public static List<String> readDiscardedFlights() {
        try {
            return Files.readAllLines(DEFAULT_DISCARDED_FLIGHTS_FILE_PATH);
        }
        catch (IOException e) {
            log.error("FileUtils read error: {}, {}", e.getMessage(), e.getStackTrace());
        }
        return new ArrayList<>();
    }

    public static List<String> readDiscardedSecondEmptyFlights() {
        try {
            return Files.readAllLines(DEFAULT_DISCARDED_SECOND_EMPTY_FLIGHTS_FILE_PATH);
        }
        catch (IOException e) {
            log.error("FileUtils read error: {}, {}", e.getMessage(), e.getStackTrace());
        }
        return new ArrayList<>();
    }

    public static List<String> readTariffRateErrors() {
        try {
            return Files.readAllLines(DEFAULT_TARIFF_RATE_ERRORS_FILE_PATH);
        }
        catch (IOException e) {
            log.error("FileUtils read error: {}, {}", e.getMessage(), e.getStackTrace());
        }
        return new ArrayList<>();
    }

    public static String getClientOrdersSqlScript() {
        try {
            return String.join("\n", Files.readAllLines(CLIENT_ORDERS_SQL_SCRIPT_PATH));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getBackButtonUrl() {
        try {
            return Files.readAllLines(DEFAULT_BACK_BUTTON_FILE_PATH).get(0);
        } catch (RuntimeException ex) {
            throw new RuntimeException("Необходимо указать URL кнопки в файле " + DEFAULT_BACK_BUTTON_FILE_PATH, ex);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getZippedLogsFolder() {
        try {
            Path p = Paths.get(DEFAULT_LOGS_FILE_PATH + ".zip");
            Files.deleteIfExists(p);
            Path zipPath = Files.createFile(p);
            try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
                try (Stream<Path> s = Files.walk(DEFAULT_LOGS_FILE_PATH)) {
                    s.filter(path -> !Files.isDirectory(path))
                            .forEach(path -> {
                                ZipEntry zipEntry = new ZipEntry(DEFAULT_LOGS_FILE_PATH.relativize(path).toString());
                                try {
                                    zs.putNextEntry(zipEntry);
                                    Files.copy(path, zs);
                                    zs.closeEntry();
                                }
                                catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                }
            }
            return Files.readAllBytes(zipPath);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] getZippedTarifficationFolder() {
        try {
            Path p = Paths.get(TARIFFICATION_DIRECTORY_PATH + ".zip");
            Files.deleteIfExists(p);
            Path zipPath = Files.createFile(p);
            try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
                try (Stream<Path> s = Files.walk(TARIFFICATION_DIRECTORY_PATH)) {
                    s.filter(path -> !Files.isDirectory(path))
                            .forEach(path -> {
                                ZipEntry zipEntry = new ZipEntry(TARIFFICATION_DIRECTORY_PATH.relativize(path).toString());
                                try {
                                    zs.putNextEntry(zipEntry);
                                    Files.copy(path, zs);
                                    zs.closeEntry();
                                }
                                catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                }
            }
            return Files.readAllBytes(zipPath);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
