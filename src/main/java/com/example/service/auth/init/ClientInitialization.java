package com.example.service.auth.init;

import com.example.service.auth.domain.Consumer;
import com.example.service.auth.domain.User;
import com.example.service.auth.repository.ConsumerRepository;
import com.example.service.auth.repository.UserRepository;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Profile("!test")
@Configuration
@EnableConfigurationProperties(ClientInitializationProperties.class)
@Slf4j
public class ClientInitialization {

  private final ClientInitializationProperties clientInitializationProperties;
  private ConsumerRepository consumerRepository;
  private UserRepository userRepository;
  private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Autowired
  public ClientInitialization(UserRepository userRepository,
      ClientInitializationProperties clientInitializationProperties,
      ConsumerRepository consumerRepository) {
    this.userRepository = userRepository;
    this.clientInitializationProperties = clientInitializationProperties;
    this.consumerRepository = consumerRepository;
  }

  private Consumer createClient(ClientInitializationProperties.AuthClient authClient) {
    log.info("Creating client for " + authClient.getName());
    Consumer consumer = new Consumer();
    consumer.setClientId(authClient.getName());
    if (authClient.getKey() != null) {
      consumer.setClientSecret(passwordEncoder.encode(authClient.getKey()));
    }
    consumer.setAuthorizedGrantTypesCsv(scrubTypes(authClient.getAuthorizedGrantTypes()));
    consumer.setScopeCsv(authClient.getScope());
    consumer.setResourceIdsCsv(authClient.getResourceIds());
    consumer.setAccessTokenValiditySeconds(authClient.getAccessTokenValiditySeconds());
    consumer.setRefreshTokenValiditySeconds(authClient.getRefreshTokenValiditySeconds());
    consumer.setAuthorityCsv(authClient.getAuthorities());
    consumer.setRegisteredRedirectUrisCsv(authClient.getRedirectUri());
    consumer.setAutoApproveCsv(authClient.getAutoApproveScopes());
    return consumer;
  }

  private String scrubTypes(final String authorizedGrantTypes) {
    String scrubbedTypes = null;
    if (authorizedGrantTypes != null) {
      String[] types = authorizedGrantTypes.split(",");
      StringBuilder sb = new StringBuilder();
      String trimmed;
      for (String type : types) {
        if (type != null) {
          trimmed = type.trim();
          if (trimmed.length() > 0) {
            if (sb.length() > 0) {
              sb.append(",");
            }
            sb.append(trimmed);
          }
        }
      }
      scrubbedTypes = sb.toString();
    }
    return scrubbedTypes;
  }

  @PostConstruct
  public void clientConfig() {
    SecurityUtils.runAs("system", "system", "ROLE_ADMIN");
    for (ClientInitializationProperties.AuthClient authClient : clientInitializationProperties
        .getClients()) {
      if (!consumerRepository.findByClientId(authClient.getName()).isPresent()) {
        consumerRepository.save(createClient(authClient));
      }
    }
    for (ClientInitializationProperties.AuthUser authUser : clientInitializationProperties
        .getAuthUsers()) {
      if (!userRepository.findByUsername(authUser.getName()).isPresent()) {
        log.info("Adding user: " + authUser.getName());
        User user = new User();
        user.setUsername(authUser.getName());
        if (authUser.getKey() != null) {
          user.setPassword(passwordEncoder.encode(authUser.getKey()));
        }
        user.setActive(Boolean.TRUE);
        user.setRoles(authUser.getAuthorities());
        userRepository.save(user);
      }
    }
  }

}