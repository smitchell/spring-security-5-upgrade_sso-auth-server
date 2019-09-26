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
    // Return this no matter what clientId is passed in.
    Consumer consumer = new Consumer();
    consumer.setScopeCsv("read,write,trust");
    consumer.setAutoApproveCsv("true"); // <---- THIS FIXED THE PERMISSION POPUP
    consumer.setAuthorizedGrantTypesCsv("password,refresh_token,authorization_code");
    consumer.setAccessTokenValiditySeconds(100);
    consumer.setRefreshTokenValiditySeconds(100);
    consumer.setClientId(clientId);
    consumer.setRegisteredRedirectUrisCsv("http://localhost:8085/login,");
    consumer.setClientSecret(new BCryptPasswordEncoder().encode("client-secret"));
    return  consumer;
  }
}
