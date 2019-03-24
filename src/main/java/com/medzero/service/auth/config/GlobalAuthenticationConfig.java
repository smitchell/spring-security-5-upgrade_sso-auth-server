package com.medzero.service.auth.config;

import com.medzero.service.auth.service.AuthUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class GlobalAuthenticationConfig extends GlobalAuthenticationConfigurerAdapter {

  private final AuthUserDetailsService authUserDetailsService;

  @Autowired
  public GlobalAuthenticationConfig(final AuthUserDetailsService authUserDetailsService) {
    this.authUserDetailsService = authUserDetailsService;
  }

  @Override
  public void init(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(authUserDetailsService)
        .passwordEncoder(new BCryptPasswordEncoder());
  }
}
