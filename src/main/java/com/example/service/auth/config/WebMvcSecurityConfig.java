package com.example.service.auth.config;

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
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Component
@EnableResourceServer
public class WebMvcSecurityConfig implements WebMvcConfigurer {

  private static final String LOGIN = "/login";

  private AuthenticationManager authenticationManager;

  @Autowired
  public WebMvcSecurityConfig(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    this.authenticationManager =  authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public ClassLoaderTemplateResolver templateResolver() {

    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

    templateResolver.setPrefix("templates/");
    templateResolver.setSuffix(".ftl");
    templateResolver.setCharacterEncoding("UTF-8");

    return templateResolver;
  }

  @Bean
  public SpringTemplateEngine templateEngine() {

    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.setTemplateResolver(templateResolver());

    return templateEngine;
  }

  @Bean
  public ViewResolver viewResolver() {

    ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();

    viewResolver.setTemplateEngine(templateEngine());
    viewResolver.setCharacterEncoding("UTF-8");

    return viewResolver;
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
            .logout()
          .permitAll()
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
