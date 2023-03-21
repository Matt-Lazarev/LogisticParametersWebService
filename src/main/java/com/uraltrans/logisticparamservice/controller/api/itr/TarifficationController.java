package com.uraltrans.logisticparamservice.controller.api.itr;

import com.uraltrans.logisticparamservice.service.itr.TarifficationService;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tariffications")
@Tag(name="Тарификация", description = "Получение данных по тарификации из СТЖ (itr) в виде архива из csv-файлов")
public class TarifficationController {
    private final TarifficationService tarifficationService;

    @GetMapping
    @Operation(summary = "Получение архива файлов, для загрузки архива необходимо перейти по данному URL")
    public ResponseEntity<?> downloadTarifficationFiles(){
        tarifficationService.writeAllTarifficationFiles();
        byte[] data = FileUtils.getZippedTarifficationDirectory();
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=tariffication-data.zip")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(data);
    }
}
