package com.example.service.auth.service;

import com.example.service.auth.domain.Consumer;
import com.example.service.auth.repository.ConsumerRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthClientDetailsService implements ClientDetailsService {

  private static final Logger LOG = LoggerFactory.getLogger(AuthClientDetailsService.class);

  private ConsumerRepository consumerRepository;

  public AuthClientDetailsService(ConsumerRepository consumerRepository) {
    this.consumerRepository = consumerRepository;
  }

  @Override
  @Transactional
  public ClientDetails loadClientByClientId(String clientId) {
    Optional<Consumer> response = consumerRepository.findByClientId(clientId);
    if (response.isPresent()) {
      return response.get();
    }
    String msg = "Couldn't find the client " + clientId + "!";
    LOG.debug(msg);
    throw new ClientRegistrationException(msg);
  }
}
