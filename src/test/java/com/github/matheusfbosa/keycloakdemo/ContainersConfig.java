package com.github.matheusfbosa.keycloakdemo;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

  private static final String POSTGRES_IMAGE = "postgres:16-alpine";
  private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:23.0.1";
  private static final String realmImportFile = "/keycloakdemo-realm.json";
  private static final String realmName = "keycloakdemo";

  @Bean
  @ServiceConnection
  public PostgreSQLContainer<?> postgres() {
    return new PostgreSQLContainer<>(POSTGRES_IMAGE);
  }

  @Bean
  public KeycloakContainer keycloak(final DynamicPropertyRegistry registry) {
    final var keycloak = new KeycloakContainer(KEYCLOAK_IMAGE).withRealmImportFile(realmImportFile);
    registry.add(
        "spring.security.oauth2.resourceserver.jwt.issuer-uri",
        () -> keycloak.getAuthServerUrl() + "/realms/" + realmName
    );
    return keycloak;
  }
}
