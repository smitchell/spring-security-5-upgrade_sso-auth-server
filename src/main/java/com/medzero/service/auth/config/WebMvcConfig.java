package com.medzero.service.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@EnableResourceServer
public class WebMvcConfig implements WebMvcConfigurer {

  private static final String LOGIN = "/login";

  private AuthenticationManager authenticationManager;

  @Autowired
  public WebMvcConfig(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    this.authenticationManager =  authenticationConfiguration.getAuthenticationManager();
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController(LOGIN).setViewName("login");
    registry.addViewController("/oauth/confirm_access").setViewName("authorize");
    registry.addViewController("/docs/").setViewName("forward:/docs/index.html");
  }

  @Bean
  public CorsFilter corsFilter() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    final CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }

  @Configuration
  @Order(-20)
  protected class LoginConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      // @formatter:off
      http
          .formLogin().loginPage(LOGIN).permitAll()
          .and()
          .requestMatchers().antMatchers(LOGIN, "/oauth/authorize", "/oauth/confirm_access")
          .and()
          .authorizeRequests().anyRequest().authenticated();
      // @formatter:on
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.parentAuthenticationManager(authenticationManager);
    }
  }
}
