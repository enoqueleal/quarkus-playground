package br.com.playground.controller;

import br.com.playground.service.RandomNamesService;
import br.com.playground.service.PeopleService;
import br.com.playground.model.Status;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class HomeController {

    @Inject
    RandomNamesService randomNamesService;

    @Inject
    PeopleService peopleService;

    @GET
    public Response home() {
        return Response.ok(new Status("up")).build();
    }

    @GET
    @Path("/random-names")
    public Response randomNames() {
        return Response.ok(randomNamesService.getRandomNamesFromDownstream()).build();
    }

    @GET
    @Path("/random-names-cached")
    public Response randomNamesCached() {
        return Response.ok(randomNamesService.getCachedResponse()).build();
    }

    @GET
    @Path("/people")
    public Response getPeople() {
        return Response.ok(peopleService.getAllPeople()).build();
    }

}
