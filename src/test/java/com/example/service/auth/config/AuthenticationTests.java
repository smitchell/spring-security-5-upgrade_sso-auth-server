package com.example.service.auth.config;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.service.auth.service.AuthClientDetailsService;
import com.example.service.auth.service.AuthUserDetailsService;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs("target/snippets")
public class AuthenticationTests {

  @MockBean
  AuthUserDetailsService authUserDetailsService;

  @MockBean
  AuthClientDetailsService authClientDetailsService;

  @MockBean
  ClientDetails clientDetails;

  @MockBean
  UserDetails userDetails;

  @Autowired
  private MockMvc mockMvc;

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

  @Test
  public void homePageProtected() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().isUnauthorized())
        .andDo(document("protected"));
  }

  @Test
  public void oauthToken() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "password");
    params.add("client_id", "dummy-client");
    params.add("client_secret", "password");
    params.add("username", "username");
    params.add("password", "password");

    MvcResult result = mockMvc.perform(post("/oauth/token")
        .with(httpBasic("dummy-client", "password"))
        .params(params))
        .andExpect(status().is2xxSuccessful())
        .andDo(document("oauth-token",
            preprocessResponse(prettyPrint()),
            requestParameters(parameterWithName("username").description("username"),
                parameterWithName("password").description("password"),
                parameterWithName("client_id").description("consumer id"),
                parameterWithName("client_secret").description("consumer secret (password) "),
                parameterWithName("grant_type")
                    .description("Oauth2 grant type being used by consumer"))))
        .andReturn();
  }

  @Test
  public void authorizationRedirects() throws Exception {
    MvcResult result = mockMvc.perform(get("/oauth/authorize"))
        .andExpect(status().isFound())
        .andExpect(header().string("Location", "http://localhost:8080/login"))
        .andDo(document("authorize"))
        .andReturn();
  }


  @Test
  public void loginSucceeds() throws Exception {
    MvcResult loginPage = mockMvc.perform(get("/login"))
        .andExpect(status().is2xxSuccessful())
        .andDo(document("login"))
        .andReturn();

    String csrf = getCsrf(loginPage.getResponse().getContentAsString());

    MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
    form.set("username", "username");
    form.set("password", "password");
    form.set("_csrf", csrf);

    mockMvc.perform(post("/login")
        .params(form))
//        .cookie(loginPage.getResponse().getCookies()))
        .andExpect(status().isFound())
        .andExpect(header().string("Location", "/"))
        .andDo(document("login-submit"))
        .andReturn();
  }

  @Test
  public void loginFailure() throws Exception {
    MvcResult loginPage = mockMvc.perform(get("/login"))
        .andExpect(status().is2xxSuccessful())
        .andReturn();

    String csrf = getCsrf(loginPage.getResponse().getContentAsString());

    MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
    form.set("username", "username");
    form.set("password", "notpassword");
    form.set("_csrf", csrf);

    MvcResult loginPost = mockMvc.perform(post("/login")
        .params(form))
//        .cookie(loginPage.getResponse().getCookies()))  // WHY ARE COOKIES NULL IN SPRING SECURITY 5.x
        .andExpect(status().isFound())
        .andExpect(header().string("Location", "/login?error"))
        .andReturn();
  }

  private String getCsrf(String soup) {
    Matcher matcher = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*")
        .matcher(soup);
    if (matcher.matches()) {
      return matcher.group(1);
    }
    return null;
  }

}