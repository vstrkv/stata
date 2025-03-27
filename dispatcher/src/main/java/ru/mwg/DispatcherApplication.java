package ru.mwg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;

@SpringBootApplication
@ConfigurationProperties(value = "application.yml")
public class DispatcherApplication {
  public static void main(String[] args) {
    SpringApplication.run(DispatcherApplication.class);
  }
}
