#Cloud Foundry Angular Example

This is one of three projects referenced by https://github.com/spring-projects/spring-security-oauth/issues/1676
1) https://github.com/smitchell/cloud-foundry-angular-example
2) https://github.com/smitchell/spring-boot-netflix-zuul-proxy
3) https://github.com/smitchell/spring-security-5-upgrade_sso-auth-server

## How to run this project locally

1) mvn spring-boot:run

## Validation

1) Open the Angular App directly: http://localhost:4200/angular-example/
2) Sign into Spring Security directly: http://localhost:8084/
1) Access the Angular App via the proxy: http://localhost:8085/angular-example/

## Actual Behavior
1) User tries to access the Angular site via an SSO-enabled proxy server, http://localhost:8085/angular-example/
2) Spring Security on the proxy server redirects the user's browser to the Login page on the authentication server.
3) The user signs in and posts the Login form to the authentication server.
4) The authentication server redirects the user to the proxy server third-party oauth page.
5) User clicks Agree
6) The http://localhost:8085/angular-example/ is displayed.

## Expected Behavior
1) User tries to access the Angular site via an SSO-enabled proxy server, http://localhost:8085/angular-example/
2) Spring Security on the proxy server redirects the user's browser to the Login page on the authentication server.
3) The user signs in and posts the Login form to the authentication server.
4) The authentication server redirects the user to http://localhost:8085/angular-example/.

## Illustrating the Problem

To see the redirect out in action, remove "http://localhost:8085/login" from line 25 of the AuthClientDetailsService class.
Restart the auth server and try the steps above again. You will get his error.

```error="invalid_grant", error_description="Invalid redirect: http://localhost:8085/login does not match one of the registered values.```

This shows that the authentication server redirects the user to "http://localhost:8085/login", the proxy server /login, after the user 
successfully posts the authentication server /login, "http://localhost:8084/login", aka "http://auth-example/login".

