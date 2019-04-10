package com.example.service.auth.controller;

import com.example.service.auth.domain.User;
import com.example.service.auth.repository.UserRepository;
import com.example.service.auth.validation.ValidationRegex;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.apache.commons.validator.routines.RegexValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private static final String PASSWORD = "PASSWORD";
  private static final String USER_ID = "USER_ID";
  private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
  private static final String MISSING_PARAM_MSG = "%s must not be null.";
  private UserRepository userRepository;

  @Autowired
  public UserController(
      final UserRepository userRepository
  ) {
    this.userRepository = userRepository;
  }

  @PostConstruct
  public void postConstruct() {
    User user = null;
    Optional<User> optional = userRepository.findById("steve");
    if (optional.isPresent()) {
      user = optional.get();
    } else {
      // Add a user of testing
      user = new User();
      user.setUsername("steve");
      user.setPassword(new BCryptPasswordEncoder().encode("password"));
      user.setActive(true);
      user.setRoles("USER");
      user.setFirstName("Steve");
      user.setLastName("Mitchell");
      user = userRepository.save(user);
    }
    Assert.notNull(user, "User \"user\" must not be null");
  }

  @RequestMapping(value = "/users/updateUserPassword", method = RequestMethod.POST)
  public Optional<User> updateUserPassword(@RequestHeader(USER_ID) String username, @RequestHeader(PASSWORD) String password) {
    try {
      Assert.hasText(username, String.format(MISSING_PARAM_MSG, "username"));
      Assert.isTrue(new RegexValidator(ValidationRegex.UUID_PATTERN).isValid(username),
          "username is invalid: ".concat(username));
      Assert.hasText(password, String.format(MISSING_PARAM_MSG, "password"));
      Assert.isTrue(new RegexValidator(ValidationRegex.BCRYPT_PATTERN).isValid(password),
          "password is invalid: ".concat(password));
      Optional<User> user = userRepository.findByUsername(username);
      if (user.isPresent()) {
        user.get().setPassword(password);
        return Optional.of(userRepository.save(user.get()));
      } else {
        LOG.debug("User not found");
        return Optional.empty();
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return Optional.empty();
    }
  }

  @RequestMapping(value = "/users/verifyUserPassword", method = RequestMethod.GET)
  public Boolean verifyUsernameAndPassword(@RequestHeader(USER_ID) String username,
      @RequestHeader(PASSWORD) String password) {
    try {
      Assert.hasText(username, String.format(MISSING_PARAM_MSG, "username"));
      Assert.isTrue(new RegexValidator(ValidationRegex.UUID_PATTERN).isValid(username),
          "username is invalid: ".concat(username));
      Assert.hasText(password, String.format(MISSING_PARAM_MSG, "password"));
      Optional<User> user = userRepository.findByUsername(username);
      if (user.isPresent()) {
        LOG.debug("User found: ", user.get());
        boolean matches = new BCryptPasswordEncoder().matches(password, user.get().getPassword());
        if (matches) {
          LOG.debug("Password matches");
          return Boolean.TRUE;
        } else {
          LOG.debug("Password does not match");
          return Boolean.FALSE;
        }
      } else {
        LOG.debug("User not found");
        return Boolean.FALSE;
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return Boolean.FALSE;
    }
  }

}
