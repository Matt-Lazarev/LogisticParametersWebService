package com.uraltrans.logisticparamservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.URISyntaxException;


@SpringBootApplication
public class LogisticParametersWebServiceApplication {

    public static void main(String[] args) throws IOException, URISyntaxException {
        ConfigurableApplicationContext run = SpringApplication.run(LogisticParametersWebServiceApplication.class, args);
    }
}


