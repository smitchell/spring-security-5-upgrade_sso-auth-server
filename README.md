# SPRING SECURITY 5.X AUTHENTICATION SERVER UPGRADE TROUBLESHOOTING

This project is meant to help troubleshoot problems encountered upgrading our authentication server from 
Spring Boot 1.5.13.RELEASE to 2.1.3.RELEASE.

## Redirects Stopped Working

Following the upgrade to Spring Boot 2.1.3.RELEASE the AuthenticationTests.loginSucceeds() and loginFailure() tests fail because there is no redirect.
Login falls through to "/," so it results in a 403 instead of a 302. 

If I deploy to Test, I get the following behavior:
1) Browser attempts a REST call to the API on the gateway server (configured as resource server)
2) Proxy service (Zuul) checks for OAuth2 token
3) Browser redirected to login page on the authentication server.
4) Browser posts credentials to the authentication server.
5) Authorization server *should* redirect to the original endpoint on the API, but instead this error is returned.

```
   OAuth Error
   
   error="invalid_request", error_description="At least one redirect_uri must be registered with the client."
```

It appears the solution is to add redirectUris() to the ClientDetailsServiceConfigurer, but that isn't an option with our configuration.

```
  @Override
  public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
    clients.withClientDetails(authClientDetailsService);
  }
```

## AuthenticationTests.authToken() has no PasswordEncoder

The authToken() test fails because "There is no PasswordEncoder mapped for the id "null". I'm not sure what has changed with 2.1.3.RELEASE to cause this test to start failing.

``` 
    java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"    
      at org.springframework.security.crypto.password.DelegatingPasswordEncoder$UnmappedIdPasswordEncoder.matches(DelegatingPasswordEncoder.java:244) ~[spring-security-core-5.1.4.RELEASE.jar:5.1.4.RELEASE]   
      at org.springframework.security.crypto.password.DelegatingPasswordEncoder.matches(DelegatingPasswordEncoder.java:198) ~[spring-security-core-5.1.4.RELEASE.jar:5.1.4.RELEASE]
```    
 	
This must be a Mockito issue because I am setting on one in the GlobalAuthenticationConfig class.

``` 
   @Override  
   public void init(AuthenticationManagerBuilder auth) throws Exception {  
       auth.userDetailsService(authUserDetailsService)  
       .passwordEncoder(new BCryptPasswordEncoder());   
   }
```	