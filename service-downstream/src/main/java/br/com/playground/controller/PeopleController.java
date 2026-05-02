package br.com.playground.controller;

import br.com.playground.model.People;
import br.com.playground.service.PeopleService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PeopleController {

    @Inject
    PeopleService peopleService;

    @GET
    public Response getAllPeople() {
        List<People> people = peopleService.getAllPeople();
        return Response.ok(people).build();
    }

    @GET
    @Path("/{id}")
    public Response getPeopleById(@PathParam("id") Long id) {
        People people = peopleService.getPeopleById(id);
        if (people != null) {
            return Response.ok(people).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response createPeople(People people) {
        People createdPeople = peopleService.createPeople(people);
        return Response.status(Response.Status.CREATED).entity(createdPeople).build();
    }

    @PUT
    @Path("/{id}")
    public Response updatePeople(@PathParam("id") Long id, People people) {
        People updatedPeople = peopleService.updatePeople(id, people.getFirstName(), people.getLastName());
        if (updatedPeople != null) {
            return Response.ok(updatedPeople).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePeople(@PathParam("id") Long id) {
        People existingPeople = peopleService.getPeopleById(id);
        if (existingPeople != null) {
            peopleService.deletePeople(id);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}