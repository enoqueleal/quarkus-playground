package br.com.playground.controller;

import br.com.playground.model.RandomNamesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class HomeControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @TestHTTPResource("/api/random-names")
    URI randomNamesUri;

    @Test
    void shouldReturnAStableRandomNamesPayload() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(randomNamesUri).GET().build();

        HttpResponse<String> firstResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> secondResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, firstResponse.statusCode());
        assertEquals(200, secondResponse.statusCode());
        assertTrue(firstResponse.headers().firstValue("content-type").orElse("").startsWith("application/json"));
        assertEquals(firstResponse.body(), secondResponse.body());

        RandomNamesResponse payload = objectMapper.readValue(firstResponse.body(), RandomNamesResponse.class);
        assertEquals("random-names", payload.resource());
        assertEquals("ready", payload.status());
        assertEquals(5, payload.total());

        List<String> names = payload.names();
        assertNotNull(names);
        assertEquals(5, names.size());
    }
}

