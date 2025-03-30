package com.market.MSA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MSAApplication {

  public static void main(String[] args) {
    SpringApplication.run(MSAApplication.class, args);
  }
}
