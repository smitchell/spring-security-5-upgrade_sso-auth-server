# SPRING SECURITY 5.X AUTHENTICATION SERVER UPGRADE TROUBLESHOOTING

This project is meant to help troubleshoot problems encountered upgrading our authentication server from 
Spring Boot 1.5.13.RELEASE to 2.1.3.RELEASE.

## Redirect Issues for AuthenticationTests.loginSucceeds() and AuthenticationTests.loginFailure()  

I didn't see any redirect plumbing for login success in the existing code. Does that just go back to the Referrer by default? 
The loginSucceeds() and loginFailure() tests fail because the forward is to "/" so it gets a 403 instead of a 302. 
How was this test passing with 1.5.13.RELEASE? You had a homePageProtected() test, so I know it was proected before. 

## AuthenticationTests.authToken() has no PasswordEncoder

The authToken() test fails because "There is no PasswordEncoder mapped for the id "null". I'm sure what has changed with 2.1.3.RELEASE.

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