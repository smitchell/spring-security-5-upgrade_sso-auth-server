# SPRING SECURITY 5.X AUTHORIZATION SERVER UPGRADE TROUBLESHOOTING

The purpose of this repository is to troubleshoot problems encountered upgrading our authorization server from Spring Boot 1.5.13.RELEASE to 2.1.3.RELEASE.

## Summary

After upgrading an existing authentication server to Spring Boot 2, the shared custom login page in the authentication service no longer redirects the user back to the original protected URI.
Actual Behavior

1) The user attempts to access a protected, proxied web resource.
2) The user is redirected to the shared, custom login page in the authentication service.
3) The user posts her credentials.
4) Instead of being redirected back to the original website, the error below displays on the page.

```
OAuth Error
error="invalid_request", error_description="At least one redirect_uri must be registered with the client."
```

## Expected Behavior

Upon successful authentication, the browser should be redirected back to the original website.
Configuration

I scoured our production authentication service for configuration settings specific to the redirect URI but found none. We use a custom ClientDetails object, and its getRegisteredRedirectUri() method always returns an empty Set. Despite that, users get redirected to the original URI after Login. That is no longer the case after upgrading to Spring Boot 2.

I have not been able to recreate the "At least one redirect_uri must be registered with the client." error with a unit test. I would like to be able to do that so I could set a breakpoint and step through what is happening.

## Version

The working production code is running 1.5.13.RELEASE. The updated code is running 2.1.3.RELEASE

## Spring Security Issue:
https://github.com/spring-projects/spring-security/issues/6758
