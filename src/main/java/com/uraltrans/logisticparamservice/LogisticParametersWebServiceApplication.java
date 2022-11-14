package com.uraltrans.logisticparamservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
public class LogisticParametersWebServiceApplication {

    public static void main(String[] args) throws IOException, URISyntaxException {
       SpringApplication.run(LogisticParametersWebServiceApplication.class, args);
    }
}
