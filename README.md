#Cloud Foundry Angular Example

This is one of three projects referenced by https://github.com/spring-projects/spring-security-oauth/issues/1676
1) https://github.com/smitchell/cloud-foundry-angular-example
2) https://github.com/smitchell/spring-boot-netflix-zuul-proxy
3) https://github.com/smitchell/spring-security-5-upgrade_sso-auth-server

## How to run this project locally

1) mvn spring-boot:run

## Validation

1) *Angular* - Open the Angular App directly: http://localhost:4200/angular-example/
2) *Authentication Server* - Get an access token from the auth server using the curl command below
3) *Proxy with SSO* - Access the Angular App via the proxy: http://localhost:8085/angular-example

```   
      curl --user zuul-proxy-example:client-secret /
      http://localhost:8084/oauth/token /
      -d 'grant_type=password&client_id=zuul-proxy-example&username=user&password=password'
```

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
