package com.medzero.service.auth.init;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
@Data
public class ClientInitializationProperties implements Serializable {

  private List<AuthClient> clients = new ArrayList<>();

  private List<AuthUser> authUsers = new ArrayList<>();

  @Data
  public static class AuthClient implements Serializable {

    private String name;
    private String key;
    private String authorities;
    private String resourceIds;
    private String authorizedGrantTypes;
    private String scope;
    private int accessTokenValiditySeconds;
    private int refreshTokenValiditySeconds;
    private String redirectUri;
    private String autoApproveScopes;
  }

  @Data
  public static class AuthUser implements Serializable {

    private String name;
    private String key;
    private String authorities;
  }
}