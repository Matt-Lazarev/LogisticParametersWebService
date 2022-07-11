package com.uraltrans.logisticparamservice.controller.mvc;

import com.uraltrans.logisticparamservice.dto.request.LoadDataRequestDto;
import com.uraltrans.logisticparamservice.service.abstr.FlightIdleService;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/flights")
@RequiredArgsConstructor
public class FlightController {
    private final FlightIdleService flightIdleService;

    @PostMapping
    public String saveLoadedData(@ModelAttribute("dto") LoadDataRequestDto dto){
        flightIdleService.saveAllFlights(dto);
        return "redirect:/home?message=success";
    }

    @GetMapping("/discarded")
    public String getDiscardedFlights(Model model){
        model.addAttribute("discarded_flights", FileUtils.readDiscardedFlights());
        return "discarded_flights";
    }
}
