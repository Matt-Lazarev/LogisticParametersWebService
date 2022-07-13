package com.uraltrans.logisticparamservice.controller.mvc;

import com.uraltrans.logisticparamservice.dto.request.LoadDataRequestDto;
import com.uraltrans.logisticparamservice.utils.EnvUtils;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ConfigurableEnvironment env;

    @GetMapping("/home")
    public String getHomePage(Model model){
        if(model.getAttribute("dto") == null){
            LoadDataRequestDto dto = EnvUtils.getRequestParams(env);
            model.addAttribute("dto", dto);
        }
        else {
            EnvUtils.updateEnvironment(env, (LoadDataRequestDto) model.getAttribute("dto"));
        }

        model.addAttribute("logs", FileUtils.readAllLogs());
        return "main";
    }
}
