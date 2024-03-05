package com.github.matheusfbosa.keycloakdemo.product;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.util.Collections.singletonList;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.matheusfbosa.keycloakdemo.ContainersConfig;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(ContainersConfig.class)
class ProductControllerTest {

  private static final String BASE_API_URL = "/api/v1";
  private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
  private static final String CLIENT_ID = "product-service";
  private static final String CLIENT_SECRET = "hJNMELnW74cGldRaztKb0OWQnv1xLCdG";

  @LocalServerPort
  private int port;

  @Autowired
  private OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

  @BeforeEach
  void setup() {
    RestAssured.port = port;
  }

  @Test
  void shouldGetProductsWithoutAuthToken() {
    when()
        .get(BASE_API_URL + "/products")
        .then()
        .statusCode(OK.value());
  }

  @Test
  void shouldGetUnauthorizedWhenCreateProductWithoutAuthToken() {
    given()
        .contentType("application/json")
        .body(
            """
                    {
                        "title": "New Product",
                        "description": "Brand New Product"
                    }
                """
        )
        .when()
        .post(BASE_API_URL + "/products")
        .then()
        .statusCode(UNAUTHORIZED.value());
  }

  @Test
  void shouldCreateProductWithAuthToken() {
    final String token = getToken();

    given()
        .header("Authorization", "Bearer " + token)
        .contentType("application/json")
        .body(
            """
                    {
                        "title": "New Product",
                        "description": "Brand New Product"
                    }
                """
        )
        .when()
        .post(BASE_API_URL + "/products")
        .then()
        .statusCode(CREATED.value());
  }

  private String getToken() {
    final var restTemplate = new RestTemplate();
    final var httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.put("grant_type", singletonList(GRANT_TYPE_CLIENT_CREDENTIALS));
    map.put("client_id", singletonList(CLIENT_ID));
    map.put("client_secret", singletonList(CLIENT_SECRET));

    final String authServerUrl =
        oAuth2ResourceServerProperties.getJwt().getIssuerUri() + "/protocol/openid-connect/token";

    final var request = new HttpEntity<>(map, httpHeaders);
    final KeyCloakToken token = restTemplate.postForObject(
        authServerUrl,
        request,
        KeyCloakToken.class);

    assert token != null;

    return token.accessToken();
  }

  private record KeyCloakToken(@JsonProperty("access_token") String accessToken) {

  }
}
