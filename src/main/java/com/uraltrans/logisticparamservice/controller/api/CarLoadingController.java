package com.uraltrans.logisticparamservice.controller.api;

import com.uraltrans.logisticparamservice.dto.LoadingUnloadingDto;
import com.uraltrans.logisticparamservice.entity.postgres.LoadingUnloadingIdle;
import com.uraltrans.logisticparamservice.service.abstr.LoadingUnloadingIdleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/result/load_days")
@RequiredArgsConstructor
public class CarLoadingController {

    private final LoadingUnloadingIdleService loadingUnloadingIdleService;

    @GetMapping
    public List<LoadingUnloadingDto> getAllCarLoadUnloadIdles(){
        return loadingUnloadingIdleService.getAllLoadingUnloadingIdles();
    }
}
