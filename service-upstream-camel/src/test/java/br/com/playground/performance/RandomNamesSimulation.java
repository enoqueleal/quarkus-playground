package br.com.playground.performance;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.headerRegex;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class RandomNamesSimulation extends Simulation {

    private static final String BASE_URL = System.getProperty("gatling.baseUrl", "http://localhost:8080");
    private static final int USERS = Integer.getInteger("gatling.users", 10);
    private static final int RAMP_SECONDS = Integer.getInteger("gatling.rampSeconds", 5);
    private static final int REPEAT_COUNT = Integer.getInteger("gatling.repeatCount", 3);

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private final ScenarioBuilder randomNamesScenario = scenario("Random names endpoint")
            .repeat(REPEAT_COUNT).on(
                    exec(
                            http("GET /api/random-names")
                                    .get("/api/random-names")
                                    .check(status().is(200))
                                    .check(headerRegex("content-type", "application/json.*"))
                                    .check(jsonPath("$.resource").is("random-names"))
                                    .check(jsonPath("$.status").is("ready"))
                                    .check(jsonPath("$.total").ofInt().is(5))
                                    .check(jsonPath("$.names[0]").exists())
                    )
            );

    {
        setUp(
                randomNamesScenario.injectOpen(
                        rampUsers(USERS).during(Duration.ofSeconds(RAMP_SECONDS))
                )
        ).protocols(httpProtocol);
    }
}

