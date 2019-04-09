package com.example.service.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * For configuring the end users recognized by this Authorization Server
 */
@Order(-20)
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  private static final String LOGIN = "/login";


  private final AuthenticationConfiguration authenticationConfiguration;

  @Autowired
  public WebSecurityConfig(final AuthenticationConfiguration authenticationConfiguration) {
    this.authenticationConfiguration = authenticationConfiguration;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    // @formatter:off
    http
        .requestMatchers()
            .antMatchers(LOGIN, "/oauth/authorize", "/oauth/confirm_access", "/webjars/**", "/")
            .and()
        .authorizeRequests()
             // Without this line the login page gets 401 error on /webjars/bootstrap js and css (same for "/webjars/**" above)
            .antMatchers( "/webjars/**").permitAll()
            .anyRequest().authenticated()
            .and()
        .formLogin()
            .loginPage(LOGIN)
            .permitAll()
            .and()
        .logout()
            .permitAll();

    // @formatter:on
  }

}