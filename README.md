# SPRING SECURITY REFERENCE IMPLEMENTATION

This project is my reference implementation of Spring Security 5. It is a simplified version of my production project that I can share.

## Summary

This project can be run in any of the following ways.

1) **JUnit tests** - Feel free to create a pull request with new test ideas.
2) **Run Application.java** - For basic authentication tests you can run the project in your IDE at <http://localhost:8080>.
3) **PCF DEV** - Deploy to pcf dev to do integration tests with the sister Zuul Proxy project (coming soon).

### Running this Project on PCF DEV

Start pcf dev and sign in. Login with "user" and "pass".
```
cf dev start
cf login -a https://api.local.pcfdev.io --skip-ssl-validation
```
Clone the project, change to the project directory, built it, and push it to pcf dev.

```
git clone https://github.com/smitchell/spring-security-5-upgrade_sso-auth-server.git
cd [path-to-project]/spring-security-5-upgrade_sso-auth-server
mvn clean install
cf push
```

Use the PCF Console to tail the logs
1) Open your local <https://apps.local.pcfdev.io/>. Sign in with "user" "pass".
2) Navigate to the pcfdev-space. The auth-example should be running with a route of <http://auth-example.local.pcfdev.io/>
3) Click the "auth-example" hyperlink to open the app settings.
4) Select the Logs tab.

Open the application in your browser
1) Goto <http://auth-example.local.pcfdev.io>
2) Sign in as "steve" "password".
3) Click the logout button.
