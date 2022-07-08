package com.uraltrans.logisticparamservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ActionLog {

    private String action;

    private String date;

    private String success;
}
