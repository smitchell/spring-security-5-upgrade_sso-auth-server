# SPRING SECURITY 5.X AUTHORIZATION SERVER UPGRADE TROUBLESHOOTING

The purpose of this repository is to troubleshoot problems encountered upgrading our authorization server from Spring Boot 1.5.13.RELEASE to 2.1.3.RELEASE.

## 1) Redirects Stopped Working

Following the upgrade to Spring Boot 2.1.3.RELEASE the AuthenticationTests.loginSucceeds() and loginFailure() tests fail because there is no redirect.
Login falls through to "/," which is a protected resource on the authorization server, so it results in a 403 instead of a 302. 

We see a problem when we deploy the upgraded authorization server too. The web sites rely on the proxy server and authorization server for security. 
All the resource servers sit behind a Zuul proxy giving us SSO sign-on through the authorization service. When I deploy this upgraded authorization service to 
Cloud Foundry, I get the following behavior: 

1) The browser makes a REST call to a protected endpoint on the proxy server (Spring Boot 1.5 Zuul)

```
  // Proxy server where "matches" is an array of unsecured resources.
  protected void configure(HttpSecurity http) throws Exception {
      http
          .logout()
          .invalidateHttpSession(true).permitAll()
          .logoutSuccessUrl(logoutSuccessUrl)
          .and()
          .authorizeRequests()
          .antMatchers(matches).permitAll()
          .anyRequest().authenticated();
      ;
    }
```

2) The browser is redirected to that authorization server Login page
4) User posts credentials to the login endpoint on the authorization server.
5) Authorization server *should* redirect browser back to the site home page, but doesn't.

After the POST to /login here is the URL and the error. Subsequent attempts to access the site bypass the Login page and go directly to the error.

```
https://auth-service-test-examle.cfapps.io/oauth/authorize?client_id=proxy-service&redirect_uri=http://test.example.com/login&response_type=code&state=QihbF4

   OAuth Error
   
   error="invalid_request", error_description="At least one redirect_uri must be registered with the client."
```

It appears one solution is to add redirectUris() to the ClientDetailsServiceConfigurer, but we were not doing that with 1.5.13.RELEASE and everything worked.
Also, that redirectUris() method doesn't exist on ClientDetailsServiceConfigurer.withClientDetails(). I'm sure what needs to be called on the ClientDetailsServiceConfigurer
to get to the redirectUris() method when using withClientDetails(). 

```
  @Override
  public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
    clients.withClientDetails(authClientDetailsService);
  }
```

Honestly, I don't know what header was used before with Spring Boot 1.5.13.RELEASE for redirects. I assume that the REFERRER header was used for the redirect. Is that right? All I 
know is that a successful login the user ended up back on the homepage of whichever site he/she tried to access. That no longer works.

## 2) AuthenticationTests.authToken() has no PasswordEncoder

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