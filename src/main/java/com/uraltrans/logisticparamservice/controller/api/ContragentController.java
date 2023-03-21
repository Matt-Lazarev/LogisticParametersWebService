package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.common.LoadDataResponse;
import com.uraltrans.logisticparamservice.entity.postgres.Contragent;
import com.uraltrans.logisticparamservice.service.postgres.abstr.ContragentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contragents")
@Tag(name="Контрагенты", description = "Операции по контрагентам из СТЖ (itr)")
public class ContragentController {
    private final ContragentService contragentService;

    @GetMapping
    public List<Contragent> getAllContragents(){
        return contragentService.getAllContragents();
    }

    @PostMapping
    public ResponseEntity<LoadDataResponse> saveAllContragents(){
        contragentService.saveAllContragents();
        return ResponseEntity.ok(LoadDataResponse.builder().success("true").message("Данные выгружены").build());
    }
}
