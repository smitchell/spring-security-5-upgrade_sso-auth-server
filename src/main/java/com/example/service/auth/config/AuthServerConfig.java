/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.service.auth.config;

import com.example.service.auth.service.AuthClientDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * An instance of Legacy Authorization Server (spring-security-oauth2) that uses a single,
 * not-rotating key and exposes a JWK endpoint.
 *
 * See
 * <a
 * 	target="_blank"
 * 	href="https://docs.spring.io/spring-security-oauth2-boot/docs/current-SNAPSHOT/reference/htmlsingle/">
 * 	Spring Security OAuth Autoconfig's documentation</a> for additional detail
 *
 * @author Josh Cummings
 * @since 5.1
 */
@Slf4j
@Configuration
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

  private final AuthClientDetailsService authClientDetailsService;

  @Autowired
  public AuthServerConfig(
      final AuthClientDetailsService authClientDetailsService,
      final AuthenticationConfiguration authenticationConfiguration) throws Exception {
    this.authClientDetailsService = authClientDetailsService;
  }

  @Bean
  UserDetailsService memory() {
    return new InMemoryUserDetailsManager();
  }
  @Bean
  InitializingBean initializing(UserDetailsManager manager) {
    return () -> {
      UserDetails josh = User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build();
      manager.createUser(josh);
    };
  }

  @Override
  public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
    clients.withClientDetails(authClientDetailsService);
  }

}


