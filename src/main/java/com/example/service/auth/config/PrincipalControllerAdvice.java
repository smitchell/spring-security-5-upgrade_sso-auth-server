package com.example.service.auth.config;

import java.security.Principal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class PrincipalControllerAdvice {
  @ModelAttribute("currentUser")
  Principal principal(Principal p) {
    return p;
  }
}
