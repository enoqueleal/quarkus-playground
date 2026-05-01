package br.com.playground.service;

import br.com.playground.model.RandomNamesResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class DownstreamService {

    @ConfigProperty(name = "downstream.url", defaultValue = "http://localhost:8080")
    String downstreamUrl;

    private final Client client = ClientBuilder.newClient();

    public RandomNamesResponse getRandomNamesFromDownstream() {
        try {
            Response response = client.target(downstreamUrl)
                    .path("/api/random-names")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {
                return response.readEntity(RandomNamesResponse.class);
            } else {
                throw new RuntimeException("Failed to call downstream service: " + response.getStatus());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error calling downstream service", e);
        }
    }
}
