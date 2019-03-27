package com.example.service.auth.service;

import com.example.service.auth.domain.User;
import com.example.service.auth.repository.UserRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthUserDetailsService implements UserDetailsService {

  private static final Logger LOG = LoggerFactory.getLogger(AuthUserDetailsService.class);

  private final UserRepository userRepository;

  @Autowired
  public AuthUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * When getting a token for the first time, user will use their email or mobile as username When
   * using the refresh token, the username will be the UUID
   */
  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) {
    Optional<User> response = userRepository.findByUsername(username);
    if (response.isPresent()) {
      return response.get();
    }
    String msg = "Couldn't find the username " + username + "!";
    LOG.debug(msg);
    throw new UsernameNotFoundException(msg);
  }

}