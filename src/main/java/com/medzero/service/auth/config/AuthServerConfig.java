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
package com.medzero.service.auth.config;

import com.medzero.service.auth.service.AuthClientDetailsService;
import com.medzero.service.auth.service.AuthUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

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
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

  private final String privateKey;

  private final String publicKey;

  private final AuthClientDetailsService authClientDetailsService;

  private final AuthenticationManager authenticationManager;

  private final AuthUserDetailsService authUserDetailsService;

  @Autowired
  public AuthServerConfig(
      @Value("${keyPair.privateKey}") final String privateKey,
      @Value("${keyPair.publicKey}") final String publicKey,
      final AuthClientDetailsService authClientDetailsService,
      final AuthUserDetailsService authUserDetailsService,
      final AuthenticationConfiguration authenticationConfiguration) throws Exception {
    this.privateKey = privateKey;
    this.publicKey = publicKey;
    this.authClientDetailsService = authClientDetailsService;
    this.authUserDetailsService = authUserDetailsService;
    this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
  }

  @Override
  public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
    clients.withClientDetails(authClientDetailsService);
  }

  @Override
  public void configure(final AuthorizationServerEndpointsConfigurer endpoints) {
    endpoints
        .authenticationManager(authenticationManager)
        .accessTokenConverter(accessTokenConverter())
        .userDetailsService(authUserDetailsService)
        .tokenStore(tokenStore());
  }

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}


  @Bean
  public JwtAccessTokenConverter accessTokenConverter() {
    final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    converter.setSigningKey(privateKey);
    converter.setVerifierKey(publicKey);
    return converter;
  }

}


