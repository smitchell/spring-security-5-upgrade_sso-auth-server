package com.example.service.auth.config;

import com.example.service.auth.domain.Consumer;
import com.example.service.auth.service.AuthClientDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static junit.framework.TestCase.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationTests {

  @MockBean
  AuthClientDetailsService authClientDetailsService;

  ClientDetails clientDetails;

  @Autowired
  private MockMvc mockMvc;

  @Before
  public void before() {
    Consumer consumer = new Consumer();
    consumer.setScopeCsv("read,write");
    consumer.setAuthorizedGrantTypesCsv("password,refresh_token,authorization_code");
    consumer.setAccessTokenValiditySeconds(100);
    consumer.setRefreshTokenValiditySeconds(100);
    consumer.setClientId("dummy-client");
    consumer.setRegisteredRedirectUrisCsv("https://test.domain.com/context1,https://test.domain.com/context2,https://test.domain.com/context3");
    consumer.setClientSecret(new BCryptPasswordEncoder().encode("password"));
    this.clientDetails = consumer;

    Mockito.when(this.authClientDetailsService.loadClientByClientId("dummy-client"))
        .thenReturn(clientDetails);
  }

  @Test
  public void oauthToken() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "password");
    params.add("client_id", "dummy-client");
    params.add("client_secret", "password");
    params.add("username", "steve");
    params.add("password", "password");

    MvcResult result = mockMvc.perform(post("/oauth/token")
        .with(httpBasic("dummy-client", "password"))
        .params(params))
        .andExpect(status().is2xxSuccessful())
        .andReturn();
  }

  @Test
  public void authorizationRedirects() throws Exception {
    MvcResult result = mockMvc.perform(get("/oauth/authorize"))
        .andExpect(status().isFound())
//        .andExpect(header().string("Location", "http://localhost:8080/login"))
        .andExpect(header().string("Location", "http://localhost/login"))
        .andReturn();
  }

  @Test
  public void loginSucceeds() throws Exception {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
    form.set("username", "steve");
    form.set("password", "password");

    mockMvc.perform(post("/login")
        .params(form).with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/"))
        .andReturn();
  }

  @Test
  @WithMockUser
  public void authorizationRedirectsAfterLogin() throws Exception {
    mockMvc.perform(get("/oauth/authorize")
            .param("client_id", "dummy-client")
            .param("response_type", "code")
            .param("redirect_uri", "https://test.domain.com/context2")
            .with(user("username")))
            .andExpect(forwardedUrl("/oauth/confirm_access"));
  }

  /**
   * The purpose of this test is to verify that CSRF is being populated on the custom login form.
   * <pre>
   *       &lt;input type="hidden" id="csrf_token" name="${_csrf.parameterName}" value="${_csrf.token}" /&gt;
   * </pre>
   */
  @Test
  public void verifyCors() throws Exception {
    MvcResult loginPage = mockMvc.perform(get("/login"))
        .andExpect(status().is2xxSuccessful())
        .andReturn();


    String cors = getCorsId(loginPage.getResponse().getContentAsString());
    assertNotNull(cors);
  }

  @Test
  public void loginFailure() throws Exception {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
    form.set("username", "steve");
    form.set("password", "notpassword");

    mockMvc.perform(post("/login")
        .params(form).with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(cookie().exists("SESSION"))
        .andExpect(status().isFound())
        .andExpect(header().string("Location", "/login?error"))
        .andReturn();
  }

  private String getCorsId(String soup) {
    Matcher matcher = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*")
        .matcher(soup);
    if (matcher.matches()) {
      return matcher.group(1);
    }
    return null;
  }

}
