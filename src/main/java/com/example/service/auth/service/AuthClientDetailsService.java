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

  private final String authServiceUrl;
  private final String proxyServiceUrl;

  @Autowired
  public AuthClientDetailsService(
      @Value("${example.auth-url}") final String authServiceUrl,
      @Value("${example.proxy-url}") final String proxyServiceUrl
  ) {
    this.authServiceUrl = authServiceUrl;
    this.proxyServiceUrl = proxyServiceUrl;
  }

  /**
   * This is a quick and dirty convience method to set-up the app for the proxy integration test.
   * @return
   */
  private String generateRegisteredRedirectUrisCsv() {
    StringBuilder sb = new StringBuilder();
    for (String value : new String[]{"/", "/login", "/angular-example"}) {
      sb.append(authServiceUrl);
      sb.append(value);
      sb.append(",");
      sb.append(proxyServiceUrl);
      sb.append(value);
      sb.append(",");
    }
    return sb.toString().substring(0, sb.toString().length() - 1);
  }

  @Override
  @Transactional
  public ClientDetails loadClientByClientId(String clientId) {
    Consumer consumer = new Consumer();
    consumer.setScopeCsv("read,write");
    consumer.setAuthorizedGrantTypesCsv("password,refresh_token,authorization_code");
    consumer.setAccessTokenValiditySeconds(100);
    consumer.setRefreshTokenValiditySeconds(100);
    consumer.setClientId(clientId);
    consumer.setRegisteredRedirectUrisCsv(generateRegisteredRedirectUrisCsv());
    consumer.setClientSecret(new BCryptPasswordEncoder().encode("password"));
    return  consumer;
  }
}
