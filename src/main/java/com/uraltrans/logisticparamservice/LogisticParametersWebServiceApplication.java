package com.uraltrans.logisticparamservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class LogisticParametersWebServiceApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(LogisticParametersWebServiceApplication.class, args);
    }
}
