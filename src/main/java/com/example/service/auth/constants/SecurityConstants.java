package com.example.service.auth.constants;

public class SecurityConstants {
  public static final String LOGIN = "/login";
  public static final String AUTH_LOGIN_URL = "/api/authenticate";
  public static final String OAUTH_AUTHORIZE_URL = "/oauth/authorize";
  public static final String OAUTH_CONFIRM_ACCESS_URL = "/oauth/confirm_access";
  public static final String OAUTH_TOKEN_URL = "/oauth/token";

  // JWT token defaults
  public static final String TOKEN_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String TOKEN_TYPE = "JWT";
  public static final String TOKEN_ISSUER = "secure-api";
  public static final String TOKEN_AUDIENCE = "secure-app";
}
