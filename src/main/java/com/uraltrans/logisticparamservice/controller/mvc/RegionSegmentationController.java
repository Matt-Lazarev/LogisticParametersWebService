package com.uraltrans.logisticparamservice.controller.mvc;

import com.uraltrans.logisticparamservice.dto.ratetariff.TariffResultResponse;
import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationParameters;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionSegmentationParametersService;
import com.uraltrans.logisticparamservice.service.postgres.abstr.RegionSegmentationT15Service;
import com.uraltrans.logisticparamservice.service.postgres.abstr.TariffResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Slf4j
@Controller
@RequestMapping("/segmentation")
@RequiredArgsConstructor
public class RegionSegmentationController {
    private final RegionSegmentationParametersService regionSegmentationParametersService;
    private final RegionSegmentationT15Service regionSegmentationT15Service;
    private final TariffResponseService tariffResponseService;

    @GetMapping
    public String getSegmentationPage(Model model){
        if(model.getAttribute("dto") != null){
            regionSegmentationParametersService.updateParameters((RegionSegmentationParameters) model.getAttribute("dto"));
        }

        RegionSegmentationParameters parameters = regionSegmentationParametersService.getParameters();
        model.addAttribute("dto", parameters);
        return "html/region-segmentation-main";
    }

    @PostMapping
    public String saveSegmentation(@ModelAttribute("dto") RegionSegmentationParameters dto,
                                   @RequestParam String action, RedirectAttributes redirectAttrs){

        if(action.equals("save")){
            regionSegmentationT15Service.saveAllRegionSegmentationsT15();
        }

        regionSegmentationParametersService.updateParameters(dto);
        redirectAttrs.addFlashAttribute("dto", dto);
        redirectAttrs.addFlashAttribute("message", "success");
        redirectAttrs.addFlashAttribute("service", "region-segmentation");
        return "redirect:/segmentation";
    }

    @PostMapping(value = "/tariff", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public void takeTariffResponse(@RequestBody TariffResultResponse response){
        tariffResponseService.saveAllTariffResponses(response);
        log.info("responses rate:\n UID: {} \n body: {}", response.getUid(), response);
    }
}
