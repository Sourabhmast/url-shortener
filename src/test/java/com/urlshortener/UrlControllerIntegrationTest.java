package com.urlshortener;

import com.urlshortener.dto.ShortenRequest;
import com.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UrlControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("urlshortener")
            .withUsername("test")
            .withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UrlRepository urlRepository;

    @BeforeEach
    void setUp() {
        urlRepository.deleteAll();
    }

    @Test
    void testShortenAndRedirect() {
        // 1. Shorten URL
        ShortenRequest request = new ShortenRequest();
        request.setOriginalUrl("https://spring.io");

        ResponseEntity<String> shortenResponse = restTemplate.postForEntity("/api/shorten", request, String.class);
        assertEquals(HttpStatus.OK, shortenResponse.getStatusCode());
        assertNotNull(shortenResponse.getBody());
        assertTrue(shortenResponse.getBody().contains("shortCode"));
        
        // Extract short code (basic extraction for test)
        String body = shortenResponse.getBody();
        String shortCode = body.substring(body.indexOf("\"shortCode\":\"") + 13, body.indexOf("\"", body.indexOf("\"shortCode\":\"") + 13));

        // 2. Redirect
        ResponseEntity<Void> redirectResponse = restTemplate.getForEntity("/r/" + shortCode, Void.class);
        assertEquals(HttpStatus.FOUND, redirectResponse.getStatusCode());
        assertEquals("https://spring.io", redirectResponse.getHeaders().getLocation().toString());
        
        // 3. Get Stats
        ResponseEntity<String> statsResponse = restTemplate.getForEntity("/api/stats/" + shortCode, String.class);
        assertEquals(HttpStatus.OK, statsResponse.getStatusCode());
        assertTrue(statsResponse.getBody().contains("\"clickCount\":1"));
    }
}
