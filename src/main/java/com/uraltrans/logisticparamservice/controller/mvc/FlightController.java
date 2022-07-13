package com.uraltrans.logisticparamservice.controller.mvc;

import com.uraltrans.logisticparamservice.dto.request.LoadDataRequestDto;
import com.uraltrans.logisticparamservice.service.abstr.FlightIdleService;
import com.uraltrans.logisticparamservice.service.abstr.FlightService;
import com.uraltrans.logisticparamservice.service.abstr.FlightTimeDistanceService;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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

    @PostMapping("/save_data")
    @Transactional
    public String saveLoadedData(@ModelAttribute("dto") LoadDataRequestDto dto,
                                 @RequestParam String action, RedirectAttributes redirectAttrs){

        if(action.equals("save")){
            flightService.saveAllFlights(dto);
            flightIdleService.saveAll(dto);
            flightTimeDistanceService.saveAll(dto);
        }

        redirectAttrs.addFlashAttribute("dto", dto);
        redirectAttrs.addFlashAttribute("message", "success");
        return "redirect:/home";
    }

    @GetMapping("/discarded")
    public String getDiscardedFlights(Model model){
        model.addAttribute("discarded_flights", FileUtils.readDiscardedFlights());
        return "discarded_flights";
    }
}
