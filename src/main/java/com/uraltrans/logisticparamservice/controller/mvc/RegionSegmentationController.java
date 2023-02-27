package com.uraltrans.logisticparamservice.controller.mvc;

import com.uraltrans.logisticparamservice.dto.ratetariff.TariffResultResponse;
import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationLog;
import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationParameters;
import com.uraltrans.logisticparamservice.service.postgres.abstr.*;
import com.uraltrans.logisticparamservice.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;


@Slf4j
@Controller
@RequestMapping("/segmentation")
@RequiredArgsConstructor
public class RegionSegmentationController {
    private final RegionSegmentationParametersService regionSegmentationParametersService;
    private final RegionSegmentationCollapsedT15Service regionSegmentationCollapsedT15Service;
    private final SegmentationResultT15Service segmentationResultT15Service;
    private final RegionFlightService regionFlightService;
    private final RegionSegmentationLogService regionSegmentationLogService;

    @GetMapping
    public String getSegmentationPage(Model model){
        if(model.getAttribute("dto") != null){
            regionSegmentationParametersService.updateParameters((RegionSegmentationParameters) model.getAttribute("dto"));
        }

        RegionSegmentationParameters parameters = regionSegmentationParametersService.getParameters();
        model.addAttribute("dto", parameters);
        model.addAttribute("logs", FileUtils.readAllRegionSegmentationLogs());
        return "html/region-segmentation-main";
    }

    @GetMapping("/logs/{logId}")
    public String getLogInfo(@PathVariable String logId, Model model){
        Optional<RegionSegmentationLog> logOptional = regionSegmentationLogService.getLogById(logId);
        String text = logOptional.isPresent() ? logOptional.get().getMessage() : "";
        model.addAttribute("text", text);
        return "html/region-segmentation-log";
    }
    @PostMapping
    public String saveSegmentation(@ModelAttribute("dto") RegionSegmentationParameters dto,
                                   @RequestParam String action, RedirectAttributes redirectAttrs){

        if(action.equals("save")){
            String logId = regionSegmentationLogService.saveLog();
            regionFlightService.saveAllRegionFlights(logId, false);
        }

        regionSegmentationParametersService.updateParameters(dto);
        redirectAttrs.addFlashAttribute("dto", dto);
        redirectAttrs.addFlashAttribute("message", "success");
        redirectAttrs.addFlashAttribute("service", "region-segmentation");
        return "redirect:/segmentation";
    }

    @ResponseBody
    @PostMapping(value = "/tariff", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public void takeTariffResponse(@RequestBody TariffResultResponse response){
        log.info("responses tariff:\n UID: {} \n body: {}", response.getUid(), response);

        String logId = response.getUid();
        String message = String.format("[Расчет времени в пути]: Получена информация от 1С по %d рейсам", response.getDetails().size());
        regionSegmentationLogService.updateLogMessageById(logId, message);

        regionFlightService.updateTravelTime(response);
        regionSegmentationCollapsedT15Service.saveAllRegionSegmentationsAnalysisT15(logId);
        segmentationResultT15Service.saveAllSegments(logId);
    }
}
