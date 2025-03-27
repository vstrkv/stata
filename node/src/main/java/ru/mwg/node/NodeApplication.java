package ru.mwg.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;

@SpringBootApplication
@ConfigurationProperties(value = "application.yml")
public class NodeApplication {

  public static void main(String[] args) {
    SpringApplication.run(NodeApplication.class);
  }
}
