package com.uraltrans.logisticparamservice.dto.currency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyDto {

    @JsonProperty("NumCode")
    private String numCode;

    @JsonProperty("CharCode")
    private String charCode;

    @JsonProperty("Name")
    private String name;

    private BigDecimal value;

    @JsonProperty("Value")
    private void setValue(String value){
        this.value = new BigDecimal(value);
    }
}
