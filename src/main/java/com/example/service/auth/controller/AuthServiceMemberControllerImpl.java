package com.example.service.auth.controller;

import static com.example.service.auth.validation.ValidationRegex.MOBILE_PATTERN;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.service.auth.dto.UserIdDto;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.RegexValidator;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class AuthServiceMemberControllerImpl implements AuthServiceMemberController {

  private static final String MEMBER_SERVICE_EMAIL_BY_ADDRESS = "http://member-service/emails/search/findByAddressAndUserIdIsNotNullAndActiveTrue";
  private static final String MEMBER_SERVICE_PHONE_BY_NUMBER = "http://member-service/phones/search/findByNumberAndUserIdIsNotNullAndActiveTrue";
  private static final String PROJECTION_EMAIL_USER = "emailUser";
  private static final String PROJECTION_PHONE_USER = "phoneUser";

  private RestTemplate restTemplate;

  public AuthServiceMemberControllerImpl(@LoadBalanced RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public Optional<String> getUsernameByEmailMobile(final String emailMobile) {
    //Validate as either emailAddress or phone number
    Assert.hasText(emailMobile, "emailMobile is required");
    if (EmailValidator.getInstance().isValid(emailMobile)) {
      return getUsernameByEmail(emailMobile);
    } else if (new RegexValidator(MOBILE_PATTERN).isValid(emailMobile)) {
      return getUsernameByMobile(emailMobile);
    }
    return Optional.empty();
  }

  private Optional<String> getUserId(UriComponentsBuilder builder) {
    log.info("getUserId <--- " + builder.toUriString());
    try {
      ResponseEntity<UserIdDto> response = restTemplate.exchange(
          builder.build().encode().toUri(),
          HttpMethod.GET,
          null,
          UserIdDto.class);
      log.info(new ObjectMapper().writeValueAsString(response));
      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        return Optional.of(response.getBody().getUserId());
      } else {
        return Optional.empty();
      }
    } catch (Exception e) {
      log.debug(e.getMessage());
      return Optional.empty();
    }
  }

  private Optional<String> getUsernameByEmail(String email) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(MEMBER_SERVICE_EMAIL_BY_ADDRESS)
        .queryParam("address", email)
        .queryParam("projection", PROJECTION_EMAIL_USER);
    return getUserId(builder);
  }

  private Optional<String> getUsernameByMobile(String mobile) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(MEMBER_SERVICE_PHONE_BY_NUMBER)
        .queryParam("number", mobile)
        .queryParam("projection", PROJECTION_PHONE_USER);
    return getUserId(builder);
  }

}