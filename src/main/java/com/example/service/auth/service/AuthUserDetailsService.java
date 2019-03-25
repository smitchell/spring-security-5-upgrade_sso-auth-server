package com.example.service.auth.service;

import com.example.service.auth.controller.AuthServiceMemberController;
import com.example.service.auth.domain.User;
import com.example.service.auth.repository.UserRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.RegexValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@ComponentScan({"com.medzero.client"})
@Service
public class AuthUserDetailsService implements UserDetailsService {

  private static final Logger LOG = LoggerFactory.getLogger(AuthUserDetailsService.class);

  private static final String MOBILE_PATTERN = "[0-9]{10}";
  private static final String UUID_PATTERN = "[0-9a-f]{8}-([0-9a-f]{4}-){3}[0-9a-f]{12}";
  private final UserRepository userRepository;
  private final AuthServiceMemberController authServiceMemberController;

  @Autowired
  public AuthUserDetailsService(UserRepository userRepository,
      AuthServiceMemberController authServiceMemberController) {
    this.userRepository = userRepository;
    this.authServiceMemberController = authServiceMemberController;
  }

  /**
   * When getting a token for the first time, user will use their email or mobile as username When
   * using the refresh token, the username will be the UUID
   */
  @Override
  @Transactional
  public UserDetails loadUserByUsername(String emailOrMobileOrUUID) {
    if (EmailValidator.getInstance().isValid(emailOrMobileOrUUID) || new RegexValidator(
        MOBILE_PATTERN).isValid(emailOrMobileOrUUID)) {
      return findByUsername(getUsernameFromUserService(emailOrMobileOrUUID));
    } else if (new RegexValidator(UUID_PATTERN).isValid(emailOrMobileOrUUID.toLowerCase())) {
      return findByUsername(emailOrMobileOrUUID);
    } else {
      final String msg = "Couldn't search for " + emailOrMobileOrUUID + "!";
      LOG.warn(msg);
      throw new UsernameNotFoundException(msg);
    }
  }

  private UserDetails findByUsername(String username) {
    Optional<User> response = userRepository.findByUsername(username);
    if (response.isPresent()) {
      return response.get();
    }
    String msg = "Couldn't find the username " + username + "!";
    LOG.debug(msg);
    throw new UsernameNotFoundException(msg);

  }

  private String getUsernameFromUserService(String emailMobile) {
    log.info("getUsernameFromUserService <--- " + emailMobile);
    Optional<String> response = authServiceMemberController.getUsernameByEmailMobile(emailMobile);
    if (response.isPresent()) {
      return response.get();
    }
    LOG.debug(emailMobile + " not found.");
    throw new UsernameNotFoundException("Not found");
  }
}