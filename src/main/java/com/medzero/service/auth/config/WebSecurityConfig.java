package com.medzero.service.auth.config;

import com.medzero.service.auth.service.AuthUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * For configuring the end users recognized by this Authorization Server
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  private final AuthUserDetailsService authUserDetailsService;

  @Autowired
  public WebSecurityConfig(AuthUserDetailsService authUserDetailsService) {
    this.authUserDetailsService = authUserDetailsService;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .mvcMatchers("/.well-known/jwks.json").permitAll()
        .anyRequest().authenticated();
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(authUserDetailsService)
        .passwordEncoder(new BCryptPasswordEncoder());
  }

  @Bean
  @Override
  public UserDetailsService userDetailsService() {
    return authUserDetailsService;
  }
}