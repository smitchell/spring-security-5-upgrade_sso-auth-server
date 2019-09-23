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

In the sequence of calls below, you see that the redirect URL is "&redirect_uri=http://localhost:8085/login"
instead of being "&redirect_uri=http://localhost:8085/angular-example". Something is setup wrong on the proxy server.

1) GET http://localhost:8085/angular-example/ 302
2) GET Location: http://localhost:8085/login 302
3) GET Location: http://localhost:8084/oauth/authorize?client_id=zuul-proxy-example&redirect_uri=http://localhost:8085/login&response_type=code&state=pgAptw 302
4) GET http://localhost:8084/login 200
5) POST http://localhost:8084/login 302 (user logs in with user/password)
6) GOT Request URL: http://localhost:8084/oauth/authorize?client_id=zuul-proxy-example&redirect_uri=http://localhost:8085/login&response_type=code&state=aFcXyH 200
7) POST http://localhost:8084/oauth/authorize (user clicks Accept on 3rd party auth page)
8) GET http://localhost:8085/login?code=9jlmH7&state=aFcXyH 302
9) GET http://localhost:8085/angular-example/ 200

In Prod, with a Spring Security 5 client on the Zuul proxy calling a Spring Security 4 Authentication Server, there is 301 on the proxy login page 
that is missing when running this code locally. The extra 301 is returned for https://hostname/login before and after the custom login page. 
Note that on step 9, the proxy server redirects to the originally requested URI. That is what is missing after upgrading the auth server 
to Spring Security 5.

1. GET https://[hostname]/[context-path] 200
2. GET https://[hostname]/login 301 Moved Permanently Location: https://hostname/login
3. GET https://[hostname]/login 302 
4. GET https://[auth server host]/oauth/authorize?client_id=[a client]&redirect_uri=http://[hostname]/login&response_type=code&state=5FYUGh 302
5. GET https://[auth server host]/login 200
6. POST https://[auth server host]/login 302 (custom login with user/password)
7. GET https://[auth server host]/oauth/authorize?client_id=[a client]&redirect_uri=http://[hostname]/login&response_type=code&state=5FYUGh 302
8. GET http://[hostname]/login?code=dzJlPi&state=5FYUGh 301
9. GET http://[hostname]/login?code=dzJlPi&state=5FYUGh 302 location: https://[hostname]/[context-path]
10. https://[hostname]/[context-path] 200

