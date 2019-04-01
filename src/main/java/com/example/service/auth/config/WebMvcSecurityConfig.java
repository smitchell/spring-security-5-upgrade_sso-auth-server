package com.example.service.auth.config;

import java.io.IOException;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@Component
@EnableResourceServer
public class WebMvcSecurityConfig implements WebMvcConfigurer {

  private static final String LOGIN = "/login";

  private AuthenticationManager authenticationManager;

  @Autowired
  public WebMvcSecurityConfig(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    this.authenticationManager =  authenticationConfiguration.getAuthenticationManager();
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController(LOGIN).setViewName("login");
    registry.addViewController("/oauth/confirm_access").setViewName("authorize");
    registry.addViewController("/docs/").setViewName("forward:/docs/index.html");
  }

//  @Bean
//  public CorsFilter corsFilter() {
//    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    final CorsConfiguration config = new CorsConfiguration();
//    config.setAllowCredentials(true);
//    config.addAllowedOrigin("*");
//    config.addAllowedHeader("*");
//    config.addAllowedMethod("*");
//    source.registerCorsConfiguration("/**", config);
//    return new CorsFilter(source);
//  }

  @Configuration
  @Order(-20)
  protected class LoginConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

      // @formatter:off
      http
          .requestMatchers().antMatchers(LOGIN, "/oauth/authorize", "/oauth/confirm_access")
            .and()
            .logout().permitAll()
            .and()
         .authorizeRequests().anyRequest().authenticated()
            .and()
         .formLogin().loginPage(LOGIN).permitAll();
      // @formatter:on
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.parentAuthenticationManager(authenticationManager);
    }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//      CorsConfiguration configuration = new CorsConfiguration();
//      configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE"));
//      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//      source.registerCorsConfiguration("/**", configuration);
//      return source;
//    }
  }


}
