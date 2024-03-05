# keycloak-demo

This is a simple demo of Keycloak, a popular open source Identity and Access Management solution.

## Spring Security

We are using Spring Security OAuth 2 features to create a [Spring Boot OAuth 2.0 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html), and protect it using OAuth Service Providers like Keycloak.

The `SecurityConfig` class configures the security filter chain to use Keycloak for authentication and authorization.

## Testing

We are using Testcontainers to run Keycloak in a Docker container for testing.
An example of Keycloak realm configuration is provided in `src/test/resources/keycloakdemo-realm.json`.

To run the tests, simply execute:

```bash
./gradlew test
```
