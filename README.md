# Proxy Example

This is a Zuul Proxy example used to set-up integration tests. Currently, I´m testing Spring Security.

### PROBLEM STATEMENT

For this integration test, create a PCF Space that includes a service registry and these micro-services:
* **Angular Example** - If you bypass the proxy and go to the angular example route the page loads. [TRY IT](https://angular-example.test.medzero.cfapps.io/).
* **Authentication Example** - If you bypass the proxy and go to the authentication server root you can sign in and access a protected page. [TRY IT](https://auth-example.test.medzero.cfapps.io/)
* **Zuul Proxy Example** - If you try to access the angular example via the proxy you do not get the expected result. [TRY IT](https://zuul-proxy-example.test.medzero.cfapps.io/angular-example/)

#### Expected Result
1) User tries to access the Angular site via an SSO-enabled proxy server.
2) Spring Security on the proxy server redirects the user´s browser to the Login page on the authentication server.
3) The user signs in and posts the Login form to the authentication server.
4) The authentication server creates a JWT oauth2 token and forwards the user to the orginally requested Angular site.

#### Actual Results
The authentication server forwards the user back to the Login page.

### How do I run this?

Since we are using a Service Registry you must set the route in the manifest.yml file of all three projects, plus you must 
set some URLs in the application.yml files of the proxy server and authentication server.

#### Create a PCF Test Space for the Demo
1) Create a new PCF test Space.
2) Add a Service Registry.
3) `cf login` or `cf t -s [space name]` to point to the new test space.

### Start all three projects to generate the routes
The easiest way to get start is to go ahead and build/push all three project for routes to be assigned.
Next, add the routes to Proxy and Auth projects as shown below:

#### Proxy Server
1) Edit manifest.yml and change the route to match the assigned route above.
```
    routes:
      - route: auth-example.cfapps.io <-- Use your assigned route
```

2) Edit /src/main/resources/application.yml and change the following properties to match your the assigned routes:
```
example:
  proxy:
    logout-url: [route assigned to your Proxy service]
```

#### Spring Security 5 Upgrade SSO Auth Server
1) Edit manifest.yml and change the route to match the assigned route above.
2) Edit /src/main/resources/application.yml and change the following properties to match your the assigned routes:
```
example:
  auth-url: [route assigned to your Auth service]
  proxy-url: [route assigned to your Proxy service]
```

#### Angular Example
1) Edit manifest.yml uncomment the route, and change it to match the assigned route above.
```
    routes:
      - route: angular-example.cfapps.io <-- Use your assigned route
```
2) Bind the angular instance to the service registry

### Test Your Setup
1) Verify the Angular app is running by hitting its assigned route (i.e. [https://angular-example.cfapps.io]). Note, if the app is bound to the service register the context, /angular-example, will get appended to the URL when the page loads.
2) Verify that the Authentication Server is running by hitting its assign route (i.e. [https://auth-example.cfapps.io/]). Sign in as steve/password.
3) Verify the proxy is running by hitting its assigned route + /angular-example (i.e. [https://zuul-proxy-example.cfapps.io/angular-example/]). <-- Note the trailing slash

It **should** load the angular page when you sign in, but as of this writing you get the /login page again instead.
You may see "Full authentication is required to access this resource" when it attempts to redisplay the /login page.


