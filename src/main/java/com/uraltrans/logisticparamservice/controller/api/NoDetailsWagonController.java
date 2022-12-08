package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.nodetailswagon.NoDetailsWagon;
import com.uraltrans.logisticparamservice.service.postgres.abstr.NoDetailsWagonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/result")
public class NoDetailsWagonController {
    private final NoDetailsWagonService noDetailsWagonService;

    @GetMapping("/noDetailsWagon")
    public ResponseEntity<List<NoDetailsWagon>> getAllNoDetailsWagons(){
        return ResponseEntity.ok(noDetailsWagonService.getAllNoDetailsWagon());
    }
}
