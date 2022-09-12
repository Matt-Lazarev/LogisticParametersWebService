package com.uraltrans.logisticparamservice.controller.mvc;

import com.uraltrans.logisticparamservice.entity.postgres.LoadParameters;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightIdleService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.FlightTimeDistanceService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.LoadParameterService;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/flights")
@RequiredArgsConstructor
public class FlightController {
    private final FlightService flightService;
    private final FlightIdleService flightIdleService;
    private final FlightTimeDistanceService flightTimeDistanceService;
    private final LoadParameterService loadParameterService;


    @PostMapping("/save_data")
    public String saveLoadedData(@ModelAttribute("dto") LoadParameters dto,
                                 @RequestParam String action, RedirectAttributes redirectAttrs){
        if(action.equals("save")){
            flightService.saveAllFlights(dto);
            flightIdleService.saveAll(dto);
            flightTimeDistanceService.saveAll(dto);
        }
        loadParameterService.updateLoadParameters(dto);
        redirectAttrs.addFlashAttribute("dto", dto);
        redirectAttrs.addFlashAttribute("message", "success");
        return "redirect:/home";
    }

    @GetMapping("/discarded")
    public String getDiscardedFlights(Model model){
        model.addAttribute("discarded_flights", FileUtils.readDiscardedFlights());
        return "html/discarded_flights";
    }

    @GetMapping("/rate-tariff-errors")
    public String getRateTariffErrors(Model model){
        model.addAttribute("errors", FileUtils.readTariffRateErrors());
        return "html/rate_tariff_errors";
    }
}
