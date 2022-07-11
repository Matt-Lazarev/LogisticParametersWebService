package com.uraltrans.logisticparamservice.controller.mvc;

import com.uraltrans.logisticparamservice.dto.request.LoadDataRequestDto;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class MainController {

    @GetMapping("/home")
    public String getHomePage(@RequestParam(required = false) String message, Model model){
        model.addAttribute("dto", new LoadDataRequestDto());
        model.addAttribute("logs", FileUtils.readAllLogs());
        model.addAttribute("message", message);
        return "main";
    }
}
