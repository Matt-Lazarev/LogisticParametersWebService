package com.uraltrans.logisticparamservice.controller.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin("*")
@Controller
@RequiredArgsConstructor
public class MapController {

    @GetMapping("/map")
    public String getMap(){
        return "html/map";
    }
}
