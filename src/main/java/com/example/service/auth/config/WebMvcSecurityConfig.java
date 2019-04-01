package com.example.service.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@EnableResourceServer
public class WebMvcSecurityConfig implements WebMvcConfigurer {

  private static final String LOGIN = "/login";

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


}
