package br.com.playground.camel;

import br.com.playground.model.RandomNamesResponse;
import br.com.playground.service.RandomNamesService;
import br.com.playground.service.PeopleService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CamelRoutes extends RouteBuilder {

    @Inject
    RandomNamesService randomNamesService;

    @Inject
    PeopleService peopleService;

    @Override
    public void configure() throws Exception {

        // Configure REST DSL
        restConfiguration()
                .component("platform-http")
                .bindingMode(RestBindingMode.json);

        // Health check endpoint
        rest("/api")
                .get()
                .to("direct:health-check");

        // Random names endpoint (calls downstream service)
        rest("/api")
                .get("/random-names")
                .to("direct:random-names");

        // Random names cached endpoint
        rest("/api")
                .get("/random-names-cached")
                .to("direct:random-names-cached");

        // People endpoint (calls downstream service)
        rest("/api")
                .get("/people")
                .to("direct:people");

        // Health check route
        from("direct:health-check")
                .setBody(constant("{\"status\":\"up\",\"architecture\":\"camel\"}"))
                .setHeader("X-Processed-By", constant("Apache-Camel"))
                .setHeader("X-Route-ID", constant("camel-health-route"));

        // Random names route
        from("direct:random-names")
                .bean(RandomNamesService.class, "getRandomNamesFromDownstream")
                .setHeader("X-Processed-By", constant("Apache-Camel"))
                .setHeader("X-Route-ID", constant("camel-random-names-route"));

        // Random names cached route
        from("direct:random-names-cached")
                .bean(RandomNamesService.class, "getCachedResponse")
                .setHeader("X-Processed-By", constant("Apache-Camel"))
                .setHeader("X-Route-ID", constant("camel-random-names-cached-route"));

        // People route
        from("direct:people")
                .bean(PeopleService.class, "getAllPeople")
                .setHeader("X-Processed-By", constant("Apache-Camel"))
                .setHeader("X-Route-ID", constant("camel-people-route"));
    }

}
