package com.uraltrans.logisticparamservice.controller.mvc;

import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final LoadParameterService loadParameterService;

    @GetMapping("/home")
    public String getHomePage(Model model){
        if(model.getAttribute("dto") != null){
            loadParameterService.updateLoadParameters((LoadParameters) model.getAttribute("dto"));
        }

        LoadParameters parameters = loadParameterService.getLoadParameters();
        model.addAttribute("dto", parameters);
        model.addAttribute("url", FileUtils.getBackButtonUrl());
        model.addAttribute("logs", FileUtils.readAllLogs());
        return "main";
    }
}
