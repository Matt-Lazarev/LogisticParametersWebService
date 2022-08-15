package com.uraltrans.logisticparamservice.exception;

import com.uraltrans.logisticparamservice.dto.station.StationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Controller
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(StationsNotFoundException.class)
    protected List<StationResponse> handleStationsNotFoundException(StationsNotFoundException ex){
        return Collections.singletonList(StationResponse
                .builder()
                .success("false")
                .errorText(ex.getMessage())
                .build());
    }

    @ResponseBody
    @ExceptionHandler({AddressApiRequestException.class, PlanFactApiRequestException.class})
    protected List<?> handleStationsNotFoundException(Exception ex){
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("errorMessage", ex.getMessage());
        errors.put("errorType", HttpStatus.BAD_REQUEST.name());
        return Collections.singletonList(errors);
    }

    @ExceptionHandler
    protected String handleAllExceptions(Exception ex, RedirectAttributes redirectAttrs) {
        log.error("error: {}, {}", ex.getMessage(), ex.getStackTrace());
        ex.printStackTrace();
        redirectAttrs.addFlashAttribute("message", "error");
        return "redirect:/home";
    }
}
