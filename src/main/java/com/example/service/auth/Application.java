package com.example.service.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.SessionAttributes;

@EnableJpaAuditing
@SpringBootApplication
@EnableDiscoveryClient
@SessionAttributes("authorizationRequest")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
