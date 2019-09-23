package com.example.service.auth.service;

import com.example.service.auth.domain.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AuthClientDetailsService implements ClientDetailsService {

  @Override
  @Transactional
  public ClientDetails loadClientByClientId(String clientId) {
    Consumer consumer = new Consumer();
    consumer.setScopeCsv("read,write");
    consumer.setAuthorizedGrantTypesCsv("password,refresh_token,authorization_code");
    consumer.setAccessTokenValiditySeconds(100);
    consumer.setRefreshTokenValiditySeconds(100);
    consumer.setClientId(clientId);
    consumer.setRegisteredRedirectUrisCsv("http://localhost:8085/,http://localhost:8085/login,http://localhost:8085/angular-example/,http://localhost:8084/login");
    consumer.setClientSecret(new BCryptPasswordEncoder().encode("password"));
    return  consumer;
  }
}
