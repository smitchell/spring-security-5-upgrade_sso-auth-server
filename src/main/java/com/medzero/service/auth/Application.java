package com.medzero.service.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttributes;

@EnableJpaAuditing
@SpringBootApplication
@Controller
@SessionAttributes("authorizationRequest")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
