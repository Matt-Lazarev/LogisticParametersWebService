package com.uraltrans.logisticparamservice.controller.utils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Tag(name = "Тестовые операции", description = "Операция по работоспособности приложения (health-check)")
public class TestController {

    @GetMapping
    @Operation(summary = "Операция для тестирования приложения при его запуске")
    public String healthCheck() {
        return "Hello";
    }
}
