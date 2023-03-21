package com.uraltrans.logisticparamservice.controller.utils;

import com.uraltrans.logisticparamservice.utils.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/utils/logs")
@Tag(name = "Логи приложения", description = "Получение архива с логами приложения")
public class FileController {

    @GetMapping
    @Operation(summary = "Получение архива логов, для загрузки архива необходимо перейти по данному URL")
    public ResponseEntity<?> downloadApplicationLogs(){
        byte[] data = FileUtils.getZippedLogsDirectory();
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=logs.zip")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(data);
    }
}
