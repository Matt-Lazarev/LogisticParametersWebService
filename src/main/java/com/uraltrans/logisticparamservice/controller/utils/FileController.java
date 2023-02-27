package com.uraltrans.logisticparamservice.controller.utils;

import com.uraltrans.logisticparamservice.utils.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/utils/logs")
public class FileController {

    @GetMapping
    public ResponseEntity<?> downloadApplicationLogs(){
        byte[] data = FileUtils.getZippedLogsDirectory();
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=logs.zip")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(data);
    }
}
