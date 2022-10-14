package com.uraltrans.logisticparamservice.utils;

import com.uraltrans.logisticparamservice.dto.logs.ActionLog;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.file.StandardOpenOption.*;

@Slf4j
public class FileUtils {
    public static final String DELIMITER = "   ";
    private static final Path DEFAULT_LOG_FILE_PATH = Paths.get("logging/action_logs.log");
    private static final int MAX_LOGS_AMOUNT = 20;

    private static final Path DEFAULT_DISCARDED_FLIGHTS_FILE_PATH = Paths.get("logging/discarded_flights.log");
    private static final Path DEFAULT_TARIFF_RATE_ERRORS_FILE_PATH = Paths.get("logging/tariff_rate_errors.log");
    private static final Path DEFAULT_BACK_BUTTON_FILE_PATH = Paths.get("button/button-url.txt");

    private static final Path CLIENT_ORDERS_SQL_SCRIPT_PATH = Paths.get("sql/client_orders_script.sql");

    static {
        try {
            if (!Files.exists(DEFAULT_LOG_FILE_PATH)) {
                Files.createFile(DEFAULT_LOG_FILE_PATH);
            }
            if (!Files.exists(DEFAULT_DISCARDED_FLIGHTS_FILE_PATH)) {
                Files.createFile(DEFAULT_DISCARDED_FLIGHTS_FILE_PATH);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void writeActionLog(String message) {
        try {
            synchronized (FileUtils.class) {
                Files.write(DEFAULT_LOG_FILE_PATH, Collections.singletonList(message), CREATE, APPEND);
            }
        } catch (IOException e) {
            log.error("FileUtils write error: {}, {}", e.getMessage(), e.getStackTrace());
        }
    }

    public static List<ActionLog> readAllLogs() {
        List<ActionLog> logs = new ArrayList<>();
        try {
            Files.readAllLines(DEFAULT_LOG_FILE_PATH)
                    .stream()
                    .map(str -> str.split(DELIMITER))
                    .map(arr -> new ActionLog(arr[0], arr[1], arr[2]))
                    .forEach(logs::add);
        } catch (IOException e) {
            log.error("FileUtils read error: {}, {}", e.getMessage(), e.getStackTrace());
        }
        Collections.reverse(logs);
        return logs.size() <= MAX_LOGS_AMOUNT ? logs : logs.subList(0, MAX_LOGS_AMOUNT);
    }

    public static void writeDiscardedFlights(List<String> discardedFlights, boolean append) {
        writeList(discardedFlights, append, DEFAULT_DISCARDED_FLIGHTS_FILE_PATH);
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
        } catch (IOException e) {
            log.error("FileUtils write error: {}, {}", e.getMessage(), e.getStackTrace());
        }
    }

    public static List<String> readDiscardedFlights() {
        try {
            return Files.readAllLines(DEFAULT_DISCARDED_FLIGHTS_FILE_PATH);
        } catch (IOException e) {
            log.error("FileUtils read error: {}, {}", e.getMessage(), e.getStackTrace());
        }
        return new ArrayList<>();
    }

    public static List<String> readTariffRateErrors() {
        try {
            return Files.readAllLines(DEFAULT_TARIFF_RATE_ERRORS_FILE_PATH);
        } catch (IOException e) {
            log.error("FileUtils read error: {}, {}", e.getMessage(), e.getStackTrace());
        }
        return new ArrayList<>();
    }

    public static String getClientOrdersSqlScript() {
        try {
            return String.join("\n", Files.readAllLines(CLIENT_ORDERS_SQL_SCRIPT_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getBackButtonUrl() {
        try {
            return Files.readAllLines(DEFAULT_BACK_BUTTON_FILE_PATH).get(0);
        } catch (RuntimeException ex) {
            throw new RuntimeException("Необходимо указать URL кнопки в файле button/button-url.txt", ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getZippedLogsFolder() {
        try{
            Path p = Paths.get("logging.zip");
            Files.deleteIfExists(p);
            Path zipPath = Files.createFile(p);
            try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
                Path pp = Paths.get("logging");
                try (Stream<Path> s = Files.walk(pp)) {
                    s.filter(path -> !Files.isDirectory(path))
                            .forEach(path -> {
                                ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                                try {
                                    zs.putNextEntry(zipEntry);
                                    Files.copy(path, zs);
                                    zs.closeEntry();
                                } catch (IOException e) {
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
