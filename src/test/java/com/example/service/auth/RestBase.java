package com.example.service.auth;

import com.example.service.auth.service.AuthClientDetailsService;
import com.example.service.auth.service.AuthUserDetailsService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@WithMockUser
public abstract class RestBase {

  @MockBean
  AuthUserDetailsService authUserDetailsService;

  @MockBean
  AuthClientDetailsService authClientDetailsService;

  @MockBean
  ClientDetails clientDetails;

  @MockBean
  UserDetails userDetails;

  @Autowired
  private WebApplicationContext context;

  @Before
  public void before() {
    Mockito.when(this.userDetails.getPassword())
        .thenReturn(new BCryptPasswordEncoder().encode("password"));
    Mockito.when(this.userDetails.isEnabled()).thenReturn(Boolean.TRUE);
    Mockito.when(this.userDetails.isAccountNonLocked()).thenReturn(Boolean.TRUE);
    Mockito.when(this.userDetails.isAccountNonExpired()).thenReturn(Boolean.TRUE);
    Mockito.when(this.userDetails.isCredentialsNonExpired()).thenReturn(Boolean.TRUE);
    Mockito.when(this.userDetails.getUsername()).thenReturn("username");
    Mockito.when(this.authUserDetailsService.loadUserByUsername("dummy-client"))
        .thenReturn(userDetails);
    Mockito.when(this.authUserDetailsService.loadUserByUsername("username"))
        .thenReturn(userDetails);

    Set<String> scopes = new HashSet<>();
    scopes.add("read");
    scopes.add("write");
    Set<String> grants = new HashSet<>();
    grants.add("password");
    grants.add("refresh_token");
    Mockito.when(this.clientDetails.getScope()).thenReturn(scopes);
    Mockito.when(this.clientDetails.getAccessTokenValiditySeconds()).thenReturn(100);
    Mockito.when(this.clientDetails.getAuthorizedGrantTypes()).thenReturn(grants);
    Mockito.when(this.clientDetails.getRefreshTokenValiditySeconds()).thenReturn(100);
    Mockito.when(this.clientDetails.getClientId()).thenReturn("dummy-client");
    Mockito.when(this.clientDetails.isSecretRequired())
        .thenReturn(Boolean.TRUE);
    Mockito.when(this.clientDetails.getClientSecret())
        .thenReturn(new BCryptPasswordEncoder().encode("password"));
    Mockito.when(this.authClientDetailsService.loadClientByClientId("dummy-client"))
        .thenReturn(clientDetails);
  }

  @Before
  public void setUp() {
    RestAssuredMockMvc.webAppContextSetup(context);
  }

}