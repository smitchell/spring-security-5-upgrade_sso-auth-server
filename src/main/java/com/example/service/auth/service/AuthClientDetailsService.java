package com.example.service.auth.service;

import com.example.service.auth.domain.Consumer;
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
    consumer.setRegisteredRedirectUrisCsv("https://test.domain.com/context1,https://test.domain.com/context2,https://test.domain.com/context3");
    consumer.setClientSecret(new BCryptPasswordEncoder().encode("password"));
    return  consumer;
  }
}
