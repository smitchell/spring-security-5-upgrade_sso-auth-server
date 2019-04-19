package com.example.service.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.web.bind.annotation.SessionAttributes;

@EnableJpaAuditing
@SpringBootApplication
@EnableDiscoveryClient
@EnableAuthorizationServer
@SessionAttributes("authorizationRequest")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
