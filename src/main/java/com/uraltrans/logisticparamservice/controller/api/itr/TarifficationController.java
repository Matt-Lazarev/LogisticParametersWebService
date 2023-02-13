package com.uraltrans.logisticparamservice.controller.api.itr;

import com.uraltrans.logisticparamservice.service.itr.TarifficationServiceImpl;

import com.uraltrans.logisticparamservice.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tariffications")
public class TarifficationController {
    private final TarifficationServiceImpl tarifficationService;

    @GetMapping
    public ResponseEntity<?> downloadTarifficationFiles(){
        tarifficationService.writeAllTarifficationFiles();
        byte[] data = FileUtils.getZippedTarifficationFolder();
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=tariffication-data.zip")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(data);
    }
}
