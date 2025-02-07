package com.market.MSA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.market.MSA")
public class MSAApplication {

  public static void main(String[] args) {
    SpringApplication.run(MSAApplication.class, args);
  }
}
