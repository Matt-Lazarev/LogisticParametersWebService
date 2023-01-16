package com.uraltrans.logisticparamservice.controller.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin("*")
@Controller
@RequiredArgsConstructor
public class MapController {
    @Value("${application.address}")
    private String applicationAddress;

    @GetMapping("/map")
    public String getMap(Model model){
        model.addAttribute("application.address", applicationAddress);
        return "html/map";
    }
}
