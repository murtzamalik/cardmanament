package com.cms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebClient(registerRestTemplate = true)
class ApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void loginReturns200AndToken() throws Exception {
        String url = "http://localhost:" + port + "/api/auth/login";
        String body = "{\"loginId\":\"admin\",\"password\":\"test123\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            new HttpEntity<>(body, headers),
            String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode root = objectMapper.readTree(response.getBody());
        assertThat(root.path("success").asBoolean()).isTrue();
        assertThat(root.path("data").path("token").asText()).isNotBlank();
        assertThat(root.path("data").path("loginId").asText()).isEqualTo("admin");
    }

    @Test
    void loginWithUsernameAliasReturns200AndToken() throws Exception {
        String url = "http://localhost:" + port + "/api/auth/login";
        String body = "{\"username\":\"admin\",\"password\":\"test123\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            new HttpEntity<>(body, headers),
            String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode root = objectMapper.readTree(response.getBody());
        assertThat(root.path("success").asBoolean()).isTrue();
        assertThat(root.path("data").path("token").asText()).isNotBlank();
    }

    @Test
    void protectedEndpointRequiresAuth() {
        String url = "http://localhost:" + port + "/api/users";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN);
    }

    @Test
    void getUsersWithTokenReturns200() throws Exception {
        String loginUrl = "http://localhost:" + port + "/api/auth/login";
        String loginBody = "{\"loginId\":\"admin\",\"password\":\"test123\"}";
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> loginResponse = restTemplate.exchange(
            loginUrl,
            HttpMethod.POST,
            new HttpEntity<>(loginBody, loginHeaders),
            String.class
        );
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode data = objectMapper.readTree(loginResponse.getBody()).path("data");
        String token = data.path("token").asText();

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(token);
        ResponseEntity<String> usersResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/users",
            HttpMethod.GET,
            new HttpEntity<>(authHeaders),
            String.class
        );
        assertThat(usersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode usersRoot = objectMapper.readTree(usersResponse.getBody());
        assertThat(usersRoot.path("success").asBoolean()).isTrue();
    }
}
