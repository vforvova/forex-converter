package com.forexconverter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ForexConverterApplication {

  public static void main(String[] args) {
    SpringApplication.run(ForexConverterApplication.class, args);
  }
}
